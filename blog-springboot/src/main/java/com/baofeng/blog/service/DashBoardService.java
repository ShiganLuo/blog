package com.baofeng.blog.service;

import com.baofeng.blog.entity.User;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminDashBoradVO.*;
import java.util.List;
public interface DashBoardService {

    /**
     * 获取博客资源较上周变化情况
     * @return
     */
    public ApiResponse<List<BlogDetailNumberResponse>> getBlogDetailNumber();

    /**
     * 获取最近七天用户增加数
     * @return
     */
    public ApiResponse<UserAddInLastWeekResponse> getUserAddInLastWeek();
}
