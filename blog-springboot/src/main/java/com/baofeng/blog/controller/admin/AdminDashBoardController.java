package com.baofeng.blog.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import java.util.List;

import com.baofeng.blog.service.DashBoardService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminDashBoradVO.*;


@RestController
@RequestMapping("/api/admin/dashBoard")
@RequiredArgsConstructor
@Validated
public class AdminDashBoardController {
    private final DashBoardService dashBoardService;

    @GetMapping("/getBlogDetailNumber")
    public ApiResponse<List<BlogDetailNumberResponse>> getBlogDetailNumber() {
        return dashBoardService.getBlogDetailNumber();
    }
    
}
