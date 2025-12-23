package com.baofeng.blog.service;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminUserAuthDTO.CaptchaAuthLonginRequest;
import com.baofeng.blog.dto.common.UtilDTO.CaptchaResponse;
import com.baofeng.blog.dto.admin.AdminLoginResponseDTO;
import com.baofeng.blog.dto.admin.AdminUserAuthDTO.EmailAuthRequest;
public interface UtilService {

    /**
     * 请求登录图形验证码
     * @return
     */
    public ApiResponse<CaptchaResponse> getCaptcha();

    /**
     * 图形验证码登录
     * @param loginDTO
     * @return
     */
    public ApiResponse<AdminLoginResponseDTO> captechaLogin(CaptchaAuthLonginRequest captchaAuthLonginRequest);
    
    /**
     * 请求验证码
     * @param emailAuthRequest
     * @return
     */
    public ApiResponse<String> EmailCodeSend(String email);

    /**
     * 邮箱验证注册
     * @param emailAuthRequest
     * @return
     */
    public ApiResponse<String> EmailAuthRegister(EmailAuthRequest emailAuthRequest);
}
