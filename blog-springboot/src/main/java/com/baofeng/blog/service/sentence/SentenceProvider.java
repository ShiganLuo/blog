package com.baofeng.blog.service.sentence;

public interface SentenceProvider {

    /** 返回一句话，失败返回 null */
    String fetch();
}
