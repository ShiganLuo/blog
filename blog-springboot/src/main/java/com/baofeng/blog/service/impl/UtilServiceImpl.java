package com.baofeng.blog.service.impl;

import com.baofeng.blog.service.UtilService;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.entity.Role;
import com.baofeng.blog.entity.User;
import com.baofeng.blog.dto.admin.AdminLoginResponseDTO;
import com.baofeng.blog.dto.admin.AdminUserAuthDTO.CaptchaAuthLonginRequest;
import com.baofeng.blog.dto.admin.AdminUserAuthDTO.EmailAuthRequest;
import com.baofeng.blog.dto.common.UtilDTO.CaptchaResponse;
import com.baofeng.blog.enums.UserStatusEnum;
import com.baofeng.blog.enums.GenderEnum;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.enums.RoleTypeEnum;
import com.baofeng.blog.mapper.UserMapper;
import com.baofeng.blog.mapper.RoleMapper;
import com.baofeng.blog.mapper.PermissionMapper;
import com.baofeng.blog.common.util.JwtTokenProviderUtil;
import com.baofeng.blog.config.JwtPropertiesConfig;


import com.github.benmanes.caffeine.cache.Cache;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import java.awt.image.BufferedImage;
import java.util.UUID;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UtilServiceImpl implements UtilService {
    // 与登录接口集成待完成
    private final DefaultKaptcha captchaProducer;
    private final Cache<String, String> codeCache;
    private final JavaMailSender mailSender;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProviderUtil jwtTokenProvider;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final PermissionMapper permissionMapper;
    private static final Logger logger = LoggerFactory.getLogger(UtilServiceImpl.class);

    public UtilServiceImpl(
        DefaultKaptcha captchaProducer, 
        Cache<String,String> codeCache, 
        JavaMailSender mailSender,
        UserMapper userMapper,
        BCryptPasswordEncoder passwordEncoder,
        RoleMapper roleMapper,
        JwtTokenProviderUtil jwtTokenProvider,
        JwtPropertiesConfig jwtProperties,
        PermissionMapper permissionMapper) {
        this.captchaProducer = captchaProducer;
        this.codeCache = codeCache;
        this.mailSender = mailSender;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleMapper = roleMapper;
        this.jwtTokenProvider = jwtTokenProvider;
        this.accessTokenExpiration = jwtProperties.getAccessTokenExpiration();
        this.refreshTokenExpiration = jwtProperties.getRefreshTokenExpiration();
        this.permissionMapper = permissionMapper;
    }
    @Override
    public ApiResponse<CaptchaResponse> getCaptcha() {
        String text = captchaProducer.createText();
        BufferedImage image = captchaProducer.createImage(text);
        String uuid = UUID.randomUUID().toString();
        codeCache.put("IMG:" + uuid, text);
        CaptchaResponse captchaResponse = new CaptchaResponse();
        captchaResponse.setCaptchaEnabled(true);
        captchaResponse.setUuid(uuid);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", bos);
        } catch (IOException e) {
            logger.error("生成验证码失败", e);
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "生成验证码失败" );
        }

        // 转 Base64
        String imgBase64 = Base64.getEncoder().encodeToString(bos.toByteArray());
        captchaResponse.setImg("data:image/jpg;base64," + imgBase64);
        return ApiResponse.success(captchaResponse);
    }

    @Override
    public ApiResponse<AdminLoginResponseDTO> captechaLogin(CaptchaAuthLonginRequest captchaAuthLonginRequest) {
        String uuid = captchaAuthLonginRequest.uuid();
        String verifyText = captchaAuthLonginRequest.verifyText();
        String realText = codeCache.getIfPresent("IMG:" + uuid);
        String username = captchaAuthLonginRequest.username();
        String password = captchaAuthLonginRequest.password();
        if (realText != null && realText.equals(verifyText)) {
            codeCache.invalidate("IMG:" + uuid);
        } else {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"图形验证码错误");
        }

        User user = userMapper.selectByUsernameOrEmail(username);
        if (user == null) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            userMapper.incrementLoginAttempts(user.getId());
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "密码错误");
        }
        if (user.getStatus() == UserStatusEnum.BANNED.getStatus()) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED, "账户被锁定");
        }

         // 更新登录信息
        userMapper.updateLoginInfo(user.getId());

        // 生成 token
        String accessToken = jwtTokenProvider.generateToken(user, accessTokenExpiration, false);
        String refreshToken = jwtTokenProvider.generateToken(user, refreshTokenExpiration, true);

        // 获取角色
        List<Role> roleList = roleMapper.selectRolesByUserId(user.getId());
        List<String> roles = new ArrayList<>(roleList.size());
        List<Long> roleIds = new ArrayList<>(roleList.size());
        for (Role role : roleList) {
            roles.add(role.getRoleName());
            roleIds.add(role.getId());
        }
        List<String> permissions;
        if (roleIds.isEmpty()) {
            // 如果用户没有角色，则权限列表为空
            permissions = Collections.emptyList();
        } else {
            // 调用批量查询方法
            permissions = permissionMapper.selectPermissionsByRoleIds(roleIds);
        }

        AdminLoginResponseDTO.User userInfo = new AdminLoginResponseDTO.User(
            user.getId(),
            user.getAvatarUrl(),
            user.getUsername(),
            user.getNickName(),
            roles,
            permissions
        );
        AdminLoginResponseDTO adminLoginResponseDTO = new AdminLoginResponseDTO(accessToken, refreshToken, userInfo);
        return ApiResponse.success(adminLoginResponseDTO);
    }

    @Override
    public ApiResponse<String> EmailCodeSend(String email){

        try {
            sendCode(email);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"验证码获取失败");
        }
        
        return ApiResponse.success("验证码获取成功,请在5min内输入验证码");


    }

    @Override
    public ApiResponse<String> EmailAuthRegister(EmailAuthRequest emailAuthRequest) {
        String email = emailAuthRequest.email();
        String username = emailAuthRequest.username();
        User user1 = userMapper.selectByUsernameOrEmail(username);
        if (user1 != null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"用户名已存在");
        }
        
        
        User user2 = userMapper.selectByUsernameOrEmail(email);
        if (user2 != null ) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"该邮箱已经注册过用户");
        }
        String verifyCode = emailAuthRequest.verifyCode();
        String realCode = codeCache.getIfPresent("MAIL:" + email);
        if (realCode != null && realCode.equals(verifyCode)) {
            codeCache.invalidate("MAIL:" + email); // 校验成功立即删除
        } else {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"验证码错误");
        }

        User user = new User();
        user.setUsername(username);
        user.setNickName(username);
        user.setPassword(passwordEncoder.encode(emailAuthRequest.password()));
        user.setEmail(email);
        user.setIsEmailVerified(true);
        user.setStatus(UserStatusEnum.ACTIVE.getStatus());
        user.setGender(GenderEnum.UNKONWN.getCode());
        int rowUpdated1 = userMapper.insertUser(user);

        if (rowUpdated1 == 0) {
            logger.error("用户插入数据库失败");
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"用户创建失败");
        }
         // 更新roles表
        Role role = roleMapper.selectRoleByRoleName(RoleTypeEnum.USER.getRole());
        if (role == null) {
            logger.info("USER不存在于roles表,创建USER角色");
            role = new Role();
            role.setRoleName(RoleTypeEnum.USER.getRole()); // 默认分配USER权限
            role.setRoleDesc(RoleTypeEnum.USER.getDescription());
            int rowUpdated = roleMapper.insertRole(role);
            if (rowUpdated > 0) {
                logger.error("USER角色创建失败");
                return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"用户创建失败");
            }
        }

        //更新user_roles表
        int rowUpdated3 = roleMapper.insertUserRole(user.getId(), role.getId());

        return rowUpdated3 > 0 
            ? ApiResponse.success("用户创建成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"用户创建失败");
        
    }
    private void sendCode(String email){
        // 1. 生成 6 位随机验证码
        String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000));

        // 2. 存入 Caffeine 缓存 (5分钟自动过期)
        codeCache.put("MAIL:" + email, code);

        // 3. 构造邮件内容
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("geleihugh@163.com"); // 必须和配置中的 username 一致
        message.setTo(email);
        message.setSubject("登录验证码");
        message.setText("您好！您的登录验证码是：" + code + "。有效期5分钟，请勿泄露。");

        // 4. 发送
        mailSender.send(message);
    }

}
