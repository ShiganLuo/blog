package com.baofeng.blog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.time.LocalDateTime;

import com.baofeng.blog.entity.User;



@Mapper
public interface UserMapper {
    /**
     * 新建用户
     * @param user
     * @return
     */
    int insertUser(User user);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user);

    /**
     * 删除用户
     * @param id
     * @return
     */
    int deleteUser(Long id);

    /**
     * 根据id获取用户信息
     * @param id
     * @return
     */
    User selectUserById(Long id);
    
    /**
     * 根据邮箱、用户名、状态获取用户信息
     * @param username
     * @param email
     * @param status
     * @return
     */
    List<User> selectByCondition(@Param("username") String username, 
                               @Param("email") String email,
                               @Param("status") String status);
    
    /**
     * 获取用户列表
     * @param offset
     * @param pageSize
     * @return
     */
    List<User> selectByPage(@Param("offset") int offset, 
                          @Param("pageSize") int pageSize);
    
    /**
     * 根据邮箱或者用户名获取用户信息
     * @param account
     * @return
     */
    User selectByUsernameOrEmail(String account);

    /**
     * 根据用户id更新用户登录信息
     * @param id
     * @return
     */
    int updateLoginInfo(Long id);

    /**
     * 则更加用户登录尝试次数
     * @param id
     * @return
     */
    int incrementLoginAttempts(Long id);

    /**
     * 选择性更新用户信息
     * @param user
     * @return
     */
    int updateUserSelective(User user);
    
    /**
     * 更新用户密码
     * @param username
     * @param newPassword
     * @return
     */
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


    /**
     * 查看指定时间时已存在的用户数
     * @param createdAt
     * @return
     */
    long selectUserCountWhenSpecifiedTime(@Param("createdAt") LocalDateTime createdAt);

    /**
     * 删除用户
     * @param userId
     * @return
     */
    int deleteUserById(Long userId);

    /**
     * 更新用户头像
     * @param id
     * @param avatarUrl
     * @return
     */
    int updateAvatarUrl(@Param("id") Long id, @Param("avatarUrl") String avatarUrl);
} 