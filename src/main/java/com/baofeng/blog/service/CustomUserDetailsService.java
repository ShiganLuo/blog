package com.baofeng.blog.service;

import com.baofeng.blog.entity.User;
import com.baofeng.blog.mapper.UserMapper;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        // 根据用户名或邮箱查询用户信息（Mapper 中的查询语句：selectByUsernameOrEmail）
        User user = userMapper.selectByUsernameOrEmail(account);
        if (user == null) {
            throw new UsernameNotFoundException("未找到用户：" + account);
        }

        // 这里假设 user.getRole() 返回的是枚举类型，需要转换成字符串角色名称
        // 如果返回的是字符串则直接拼接 "ROLE_" + user.getRole()
        String role = "ROLE_" + user.getRole().name();

        // 构造 Spring Security 的 UserDetails 对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), 
                user.getPassword(), 
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
} 