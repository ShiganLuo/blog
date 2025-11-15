package com.baofeng.blog.service;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.common.UtilDTO.CaptchaResponse;
public interface UtilService {
    public ApiResponse<CaptchaResponse> getCaptcha();
}
