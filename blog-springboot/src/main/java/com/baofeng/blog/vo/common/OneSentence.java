package com.baofeng.blog.vo.common;

import java.util.List;

import lombok.Data;
public class OneSentence {

    @Data
    public static class OneSentenceAPI{
        private String id;
        private String hitokoto;
        private String type;
        private String from;
        private String creator;
        private String created_at;
        
    }

    @Data
    public static class PoetryToday {
        private String status;
        private PoetryTodayData data;
    }

    @Data
    public static class PoetryTodayData {
        private String id;
        private String content;
        private int popularity;
        private Origin origin;
        private List<String> matchTags;
        private String recommendReasons;
        private String cacheAt;
        private String token;
        private String idAddress;
        private String warning;
    }

    @Data
    public static class Origin {
        private String title;
        private String dynasty;
        private String author;
        private List<String> content;
        private List<String> translate;
    }
}
