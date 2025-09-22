package com.baofeng.blog.service;

import com.baofeng.blog.entity.User;
import com.baofeng.blog.mapper.UserMapper;
import com.baofeng.blog.mapper.RoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(UserMapper userMapper, RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;

    }

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        // 根据用户名或邮箱查询用户信息（Mapper 中的查询语句：selectByUsernameOrEmail）
        User user = userMapper.selectByUsernameOrEmail(account);
        if (user == null) {
            logger.warn("未找到用户：" + account);
            throw new UsernameNotFoundException("未找到用户：" + account);
        }

        Collection<? extends GrantedAuthority> authorities = roleMapper.selectRolesByUserId(user.getId()).stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
            .collect(Collectors.toList());
        // 构造 Spring Security 的 UserDetails 对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), 
                user.getPassword(), 
                authorities
        );
    }
} 