package com.baofeng.blog.service;

import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminDashBoradVO.*;
import java.util.List;
public interface DashBoardService {

    public ApiResponse<List<BlogDetailNumberResponse>> getBlogDetailNumber();
    
}
