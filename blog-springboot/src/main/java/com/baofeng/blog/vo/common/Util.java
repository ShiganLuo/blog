package com.baofeng.blog.vo.common;

import lombok.Data;
public class Util {
    @Data
    public static class CaptchaResponse {
        private boolean captchaEnabled;
        private String  uuid;
        private String  img;
    }
}
