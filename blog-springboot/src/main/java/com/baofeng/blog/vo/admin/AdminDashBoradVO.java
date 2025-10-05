package com.baofeng.blog.vo.admin;

import lombok.Builder;
import lombok.Data;
import java.util.List;
public class AdminDashBoradVO {

    @Builder
    @Data
    public static class BlogDetailNumberResponse {
        private String des;
        private String icon;
        private Long startVal;
        private Long duration;
        private Long endVal;
        private String change;
    }

    @Data
    public static class UserAddInLastWeekResponse {
        private List<String> days;
        private List<Long> counts;
    }
}
