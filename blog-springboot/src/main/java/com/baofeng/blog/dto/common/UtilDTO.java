package com.baofeng.blog.dto.common;

import lombok.Data;
public class UtilDTO {
    @Data
    public static class CaptchaResponse {
        private boolean captchaEnabled;
        private String  uuid;
        private String  img;
    }
}
