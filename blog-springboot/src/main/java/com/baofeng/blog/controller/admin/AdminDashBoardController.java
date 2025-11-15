package com.baofeng.blog.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import java.util.List;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminDashBoradDTO.*;
import com.baofeng.blog.service.DashBoardService;


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
    
    @GetMapping("/getUserAddInLastWeek")
    public ApiResponse<UserAddInLastWeekResponse> getUserAddInLastWeek() {
        return dashBoardService.getUserAddInLastWeek();
    }

    @GetMapping("/getUserAddComparedToLastWeek")
    public ApiResponse<String> getUserAddComparedToLastWeek() {
        return dashBoardService.getUserAddComparedToLastWeek();
    }

    @GetMapping("/getAccessAndUserCondition")
    public ApiResponse<List<DictTemplateResponse>> getAccessAndUserCondition() {
        return dashBoardService.getAccessAndUserCondition();
    }

    @GetMapping("/getArticleAddInThisYear")
    public ApiResponse<ArticleAddInThisYearResponse> getArticleAddInThisYear() {
        return dashBoardService.getArticleAddInThisYear();
    }

    @GetMapping("/getArticleAddCompareToLastWeek")
    public ApiResponse<String> getArticleAddCompareToLastWeek() {
        return dashBoardService.getArticleAddCompareToLastWeek();
    }
}
