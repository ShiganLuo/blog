package com.baofeng.blog.service.impl;


import com.baofeng.blog.mapper.BlogSettingMapper;
import com.baofeng.blog.mapper.ArticleMapper;
import com.baofeng.blog.mapper.TagMapper;
import com.baofeng.blog.mapper.UserMapper;
import com.baofeng.blog.mapper.CategoryMapper;
import com.baofeng.blog.common.util.UrlNormalizeUtil;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.AdminConfigDetailResponse;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.SystemSettingDict;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.SystemSettingDictResponse;
import com.baofeng.blog.dto.front.FrontBlogSettingDTO.FrontBackgroundResponse;
import com.baofeng.blog.dto.front.FrontBlogSettingDTO.FrontConfigDetailResponse;
import com.baofeng.blog.dto.front.FrontBlogSettingDTO.SomeFrontInformation;
import com.baofeng.blog.entity.BlogSetting;
import com.baofeng.blog.service.BlogSettingService;
import com.baofeng.blog.enums.*;
import com.baofeng.blog.service.redis.RedisVisitCounter;

import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BlogSettingServiceImpl implements BlogSettingService {

    private final BlogSettingMapper blogSettingMapper;
    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final RedisVisitCounter redisVisitCounter;
    private static final Logger logger = LoggerFactory.getLogger(BlogSettingService.class);

    public BlogSettingServiceImpl (
        BlogSettingMapper blogSettingMapper,
        ArticleMapper articleMapper,
        TagMapper tagMapper,
        CategoryMapper categoryMapper,
        UserMapper userMapper,
        RedisVisitCounter redisVisitCounter
    ) {
        this.blogSettingMapper = blogSettingMapper;
        this.articleMapper = articleMapper;
        this.tagMapper = tagMapper;
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
        this.redisVisitCounter = redisVisitCounter;
    }

    /**
     * 获取当前登录用户的ID
     * @return 用户ID，未登录返回null
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        if (username == null || "anonymousUser".equals(username)) {
            return null;
        }
        return userMapper.getIdByUsername(username);
    }

    @Override
    public ApiResponse<String> addViews(){
        redisVisitCounter.incrSiteVisit();
        return ApiResponse.success("访问量增加成功");    
    }

    @Override
    public ApiResponse<String> initSetting(BlogSetting blogSetting){
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED, "请先登录");
        }

        // 检查该用户是否已有设置
        BlogSetting existing = blogSettingMapper.getSettingByUserId(currentUserId);
        if (existing != null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST, "博客系统设置已存在");
        }

        blogSetting.setUserId(currentUserId);
        int success = blogSettingMapper.insertSetting(blogSetting);
        return success > 0
            ? ApiResponse.success("网站初始化成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "网站初始化失败");
    }

    @Override
    public ApiResponse<String> updateSetting(BlogSetting blogSetting) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED, "请先登录");
        }

        // 查询当前用户的设置
        BlogSetting existing = blogSettingMapper.getSettingByUserId(currentUserId);
        if (existing == null) {
            // 如果没有设置，执行初始化
            blogSetting.setUserId(currentUserId);
            blogSetting.setVisitCount(0L);
            int success = blogSettingMapper.insertSetting(blogSetting);
            return success > 0
                ? ApiResponse.success("网站设置初始化成功")
                : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "网站设置初始化失败");
        }

        // 使用已有记录的id
        blogSetting.setId(existing.getId());
        blogSetting.setUserId(currentUserId);

        // URL normalize 处理
        blogSetting.setFrontHeadBackground(
                UrlNormalizeUtil.stripUrlPrefix(blogSetting.getFrontHeadBackground())
        );
        blogSetting.setLogo(
                UrlNormalizeUtil.stripUrlPrefix(blogSetting.getLogo())
        );
        blogSetting.setFavicon(
                UrlNormalizeUtil.stripUrlPrefix(blogSetting.getFavicon())
        );
        blogSetting.setAuthorAvatar(
                UrlNormalizeUtil.stripUrlPrefix(blogSetting.getAuthorAvatar())
        );
        blogSetting.setUserAvatar(
                UrlNormalizeUtil.stripUrlPrefix(blogSetting.getUserAvatar())
        );
        blogSetting.setTouristAvatar(
                UrlNormalizeUtil.stripUrlPrefix(blogSetting.getTouristAvatar())
        );

        // 使用全量更新，允许前端将字段置为空字符串来清空
        int success = blogSettingMapper.updateSettingFull(blogSetting);
        return success > 0
            ? ApiResponse.success("网站设置更新成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "网站设置更新失败");
    }

    @Override
    public ApiResponse<FrontConfigDetailResponse> getSettingByIdFront(Long id) {
        BlogSetting blogSetting = blogSettingMapper.getSettingById(id);
        if (blogSetting == null) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "博客设置不存在");
        }

        Long articleCount = articleMapper.countAllArticles();
        Long tagCount = tagMapper.countAllTags();
        Long categoryCount = categoryMapper.countAllCategories();
        Long userCount = userMapper.countAllUsers();
        FrontConfigDetailResponse detail = new FrontConfigDetailResponse();
        detail.setWebsiteTitle(blogSetting.getWebsiteTitle());
        detail.setAuthorAvatar(blogSetting.getAuthorAvatar());
        detail.setLogo(blogSetting.getLogo());
        detail.setBlog_intro(blogSetting.getWebsiteIntro());
        detail.setFrontHeadBackground(blogSetting.getFrontHeadBackground());
        detail.setNotice(blogSetting.getNotice());
        detail.setPersonal_say(blogSetting.getAuthorPersonalSay());
        detail.setGitee(blogSetting.getGitee());
        detail.setBilibili(blogSetting.getBilibili());
        detail.setGithub(blogSetting.getGithub());
        detail.setQq_group(blogSetting.getQqGroup());
        detail.setQq(blogSetting.getQq());
        detail.setWe_chat_group(blogSetting.getWechatGroup());
        detail.setWeChat(blogSetting.getWechat());
        detail.setAli_pay(blogSetting.getAlipayQrCode());
        detail.setArticleCount(articleCount);
        detail.setWe_chat_pay(blogSetting.getWechatQrCode());
        detail.setView_time(blogSetting.getVisitCount());
        detail.setCreatedAt(blogSetting.getCreatedAt());
        detail.setTagCount(tagCount);
        detail.setCategoryCount(categoryCount);
        detail.setUserCount(userCount);
        return ApiResponse.success(detail);
    }

    /**
     * 获取当前用户的博客设置（管理后台）
     */
    public ApiResponse<AdminConfigDetailResponse> getSettingByCurrentUserAdmin() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED, "请先登录");
        }
        return getSettingByIdAdmin(currentUserId);
    }

    @Override
    public ApiResponse<AdminConfigDetailResponse> getSettingByIdAdmin(Long userId) {
        BlogSetting blogSetting = blogSettingMapper.getSettingByUserId(userId);
        if (blogSetting == null) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "博客设置不存在，请先初始化");
        }
        AdminConfigDetailResponse detail = new AdminConfigDetailResponse();
        detail.setWebsiteChineseName(blogSetting.getWebsiteChineseName());
        detail.setWebsiteEnglishName(blogSetting.getWebsiteEnglishName());
        detail.setWebsiteTitle(blogSetting.getWebsiteTitle());
        detail.setWebsiteCreateTime(blogSetting.getWebsiteCreateTime());
        detail.setWebsiteIntro(blogSetting.getWebsiteIntro());

        detail.setLogo(blogSetting.getLogo());
        detail.setNotice(blogSetting.getNotice());
        detail.setFavicon(blogSetting.getFavicon());
        detail.setFrontHeadBackground(blogSetting.getFrontHeadBackground());
        detail.setIcpFilingNumber(blogSetting.getIcpFilingNumber());
        detail.setPsbFilingNumber(blogSetting.getPsbFilingNumber());

        detail.setAuthor(blogSetting.getAuthor());
        detail.setAuthorAvatar(blogSetting.getAuthorAvatar());
        detail.setAuthorIntro(blogSetting.getAuthorIntro());
        detail.setAuthorPersonalSay(blogSetting.getAuthorPersonalSay());
        detail.setUserAvatar(blogSetting.getUserAvatar());
        detail.setTouristAvatar(blogSetting.getTouristAvatar());

        detail.setGithub(blogSetting.getGithub());
        detail.setGitee(blogSetting.getGitee());
        detail.setBilibili(blogSetting.getBilibili());
        detail.setQq(blogSetting.getQq());
        detail.setQqGroup(blogSetting.getQqGroup());
        detail.setWechat(blogSetting.getWechat());
        detail.setWechatGroup(blogSetting.getWechatGroup());
        detail.setWeibo(blogSetting.getWeibo());
        detail.setCsdn(blogSetting.getCsdn());
        detail.setZhihu(blogSetting.getZhihu());
        detail.setJuejin(blogSetting.getJuejin());
        detail.setTwitter(blogSetting.getTwitter());
        detail.setStackoverflow(blogSetting.getStackoverflow());

        detail.setMultiLanguage(blogSetting.getMultiLanguage());
        detail.setIsCommentReview(blogSetting.getIsCommentReview());
        detail.setIsEmailNotice(blogSetting.getIsEmailNotice());
        detail.setAlipayQRCode(blogSetting.getAlipayQrCode());
        detail.setWeiXinQRCode(blogSetting.getWechatQrCode());
        return ApiResponse.success(detail);
    }
   
    @Override
    public ApiResponse<SystemSettingDictResponse> getSystemSettingDict(String type) {
        List<SystemSettingDict> systemSettingDicts = new ArrayList<>();
        switch (type) {
            case "articleType":
                systemSettingDicts = Arrays.stream(ArticleTypeEnum.values())
                                    .map(articleTypeEnum -> {
                                        SystemSettingDict dict = new SystemSettingDict();
                                        dict.setDictLabel(articleTypeEnum.getType());
                                        dict.setDictValue(String.valueOf(articleTypeEnum.getCode()));
                                        dict.setCssClass("article-type-tag");
                                        dict.setListClass("info");
                                        return dict;
                                    })
                                    .collect(Collectors.toList());
                break;
            case "gender":
                systemSettingDicts = Arrays.stream(GenderEnum.values())
                            .map(genderEnum -> {
                                SystemSettingDict dict = new SystemSettingDict();
                                dict.setDictLabel(genderEnum.getGender());
                                dict.setDictValue(String.valueOf(genderEnum.getCode()));
                                dict.setCssClass("gender-type-tag");
                                dict.setListClass("info");
                                return dict;
                            })
                            .collect(Collectors.toList());
                break;
            case "role":
                systemSettingDicts = Arrays.stream(RoleTypeEnum.values())
                                        .map(roleTypeEnum -> {
                                            SystemSettingDict dict = new SystemSettingDict();
                                            dict.setDictLabel(roleTypeEnum.getDescription());
                                            dict.setDictValue(roleTypeEnum.getRole());
                                            dict.setCssClass("role-type-tag");
                                            dict.setListClass("info");
                                            return dict;
                                        })
                                        .collect(Collectors.toList());
                break;
            case "userStatus":
                systemSettingDicts = Arrays.stream(UserStatusEnum.values())
                                        .map(userStatusEnum -> {
                                            SystemSettingDict dict = new SystemSettingDict();
                                            dict.setDictLabel(userStatusEnum.getDescription());
                                            dict.setDictValue(userStatusEnum.getStatus());
                                            dict.setCssClass("userStatus-type-tag");
                                            dict.setListClass("info");
                                            return dict;
                                        })
                                        .collect(Collectors.toList());
                break;
            case "FriendLinkStatus":
                systemSettingDicts = Arrays.stream(FriendLinkStatusEnum.values())
                                        .map( friendLinkStatusEnum -> {
                                            SystemSettingDict dict = new SystemSettingDict();
                                            dict.setDictLabel(friendLinkStatusEnum.getName());
                                            dict.setDictValue(String.valueOf(friendLinkStatusEnum.getCode()));
                                            dict.setCssClass("FriendLinkStatus-type-tag");
                                            dict.setListClass("info");
                                            return dict;
                                        })
                                        .collect(Collectors.toList());
                break;
            default:
                logger.info(type);
                return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"不支持的获取类型");
        }


        int total = systemSettingDicts.size();
        SystemSettingDictResponse systemSettingDictResponse = new SystemSettingDictResponse();
        systemSettingDictResponse.setList(systemSettingDicts);
        systemSettingDictResponse.setTotal(total);
        return ApiResponse.success(systemSettingDictResponse);
        
    }

    @Override
    public ApiResponse<SomeFrontInformation> getSomeFrontInformation() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED, "请先登录");
        }
        return getSomeFrontInformationById(currentUserId);
    }

    @Override
    public ApiResponse<SomeFrontInformation> getSomeFrontInformationById(Long userId) {
        BlogSetting blogSetting = blogSettingMapper.getSettingByUserId(userId);
        if (blogSetting == null) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "博客设置不存在");
        }
        SomeFrontInformation someFrontInformation = new SomeFrontInformation();
        someFrontInformation.setIcpFilingNumber(blogSetting.getIcpFilingNumber());
        someFrontInformation.setPsbFilingNumber(blogSetting.getPsbFilingNumber());
        someFrontInformation.setWebsiteChineseName(blogSetting.getWebsiteChineseName());
        someFrontInformation.setFavicon(blogSetting.getFavicon());
        return ApiResponse.success(someFrontInformation);
    }

    @Override
    public ApiResponse<FrontBackgroundResponse> getFrontBackgroud() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED, "请先登录");
        }
        return getFrontBackgroudById(currentUserId);
    }

    @Override
    public ApiResponse<FrontBackgroundResponse> getFrontBackgroudById(Long userId) {
        BlogSetting blogSetting = blogSettingMapper.getSettingByUserId(userId);
        if (blogSetting == null || blogSetting.getFrontHeadBackground() == null) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND, "前台背景图片未设置");
        }
        FrontBackgroundResponse response = new FrontBackgroundResponse();
        response.setFrontHeadBackground(blogSetting.getFrontHeadBackground());
        return ApiResponse.success(response);
    }
}
