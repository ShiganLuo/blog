package com.baofeng.blog.service;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.common.UtilDTO.CaptchaResponse;
import com.baofeng.blog.dto.admin.AdminUserAuthDTO.EmailAuthRequest;
public interface UtilService {
    public ApiResponse<CaptchaResponse> getCaptcha();
    
    /**
     * 请求验证码
     * @param emailAuthRequest
     * @return
     */
    public ApiResponse<String> EmailCodeSend(String email);

    /**
     * 邮箱验证登录
     * @param emailAuthRequest
     * @return
     */
    public ApiResponse<String> EmailAuth(EmailAuthRequest emailAuthRequest);
}
