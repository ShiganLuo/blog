package com.baofeng.blog.service;

import com.baofeng.blog.vo.common.Util.CaptchaResponse;
import com.baofeng.blog.vo.ApiResponse;
public interface UtilService {
    public ApiResponse<CaptchaResponse> getCaptcha();
}
