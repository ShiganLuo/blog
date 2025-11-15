package com.baofeng.blog.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.common.UtilDTO.CaptchaResponse;
import com.baofeng.blog.service.UtilService;
@RestController
@RequestMapping("/api/admin/util")
public class AdminUtilController {

    private final UtilService utilService;
    public AdminUtilController(UtilService utilService) {
        this.utilService = utilService;
    }
    @GetMapping("/get-captcha")
    public ApiResponse<CaptchaResponse> getCaptcha(){
        return utilService.getCaptcha();
    }
}
