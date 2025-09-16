package com.baofeng.blog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baofeng.blog.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    int insertUser(User user);
    int updateUser(User user);
    int deleteUser(Long id);
    User selectUserById(Long id);
    
    List<User> selectByCondition(@Param("username") String username, 
                               @Param("email") String email,
                               @Param("status") User.Status status);
    
    List<User> selectByPage(@Param("offset") int offset, 
                          @Param("pageSize") int pageSize);
    
    User selectByUsernameOrEmail(String account);
    int updateLoginInfo(Long id);
    int incrementLoginAttempts(Long id);
    int updateUserSelective(User user);
    int updatePassword(String username,@Param("password") String newPassword);
    //@Param("username")必须指定，因为resultmap定义好了映射java对象password
    /**
     * 根据用户名查询用户id
     */
    Long getIdByUsername(String username);

    /**
     * 获取网站用户总数
     * @return
     */
    Long countAllUsers();
} 