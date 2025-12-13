package com.baofeng.blog.common.util;

import java.util.List;

import com.baofeng.blog.dto.front.FrontArticleDTO.*;

public class ArticleConvertUtil {

    /**
     * frontArticle 、ArticleDetailResponse转换类
     * @param frontArticle
     * @return ArticleDetailResponse
     */
    public static ArticleDetailResponse convertToDetailResponse(FrontArticle frontArticle) {
        if (frontArticle == null) return null;

        ArticleDetailResponse articleDetailResponse = new ArticleDetailResponse();

        articleDetailResponse.setId(frontArticle.getId());
        articleDetailResponse.setAuthorName(
            frontArticle.getAuthor() != null ? frontArticle.getAuthor().getAuthorName() : null
        );
        articleDetailResponse.setType(frontArticle.getType());
        articleDetailResponse.setOriginUrl(frontArticle.getOriginUrl());
        articleDetailResponse.setThumbsUpTimes(frontArticle.getThumbsUpTimes());
        articleDetailResponse.setAuthorId(
            frontArticle.getAuthor() != null ? frontArticle.getAuthor().getAuthorId() : null
        );
        articleDetailResponse.setArticleContent(frontArticle.getArticleContent());
        articleDetailResponse.setArticleCover(frontArticle.getArticleCover());
        articleDetailResponse.setArticleTitle(frontArticle.getArticleTitle());
        articleDetailResponse.setViewTimes(frontArticle.getViewTimes());
        articleDetailResponse.setCreatedAt(frontArticle.getCreatedAt());
        articleDetailResponse.setUpdatedAt(frontArticle.getUpdatedAt());

        List<String> categoryList = frontArticle.getCategoryNameList();
        articleDetailResponse.setCategoryNameList(categoryList != null ? categoryList : List.of());

        List<String> tagList = frontArticle.getTagNameList();
        articleDetailResponse.setTagNameList(tagList != null ? tagList : List.of());

        return articleDetailResponse;
    }

    /**
     * frontArticle 、ArticleDetailResponse批量转换类
     * @param List<FrontArticle>
     * @return List<ArticleDetailResponse>
     */
    public static List<ArticleDetailResponse> convertToDetailResponseList(List<FrontArticle> list) {
        if (list == null || list.isEmpty()) return List.of();
        return list.stream()
               .map(ArticleConvertUtil::convertToDetailResponse)
               .toList();
}
}

