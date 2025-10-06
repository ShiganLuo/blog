package com.baofeng.blog.service;

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

    /**
     * 获取用户相比上周变化数量
     * @return
     */
    public ApiResponse<String> getUserAddComparedToLastWeek();

    /**
     * 获取博客访问及用户情况
     * @return
     */
    public ApiResponse<List<DictTemplateResponse>> getAccessAndUserCondition();

    /**
     * 获取今年各月份文章发表量情况
     * @return
     */
    public ApiResponse<ArticleAddInThisYearResponse> getArticleAddInThisYear();

    /**
     * 获取文章相比上周变化量
     * @return
     */
    public ApiResponse<String> getArticleAddCompareToLastWeek();
}
