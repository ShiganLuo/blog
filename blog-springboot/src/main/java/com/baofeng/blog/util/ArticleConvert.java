package com.baofeng.blog.util;

import com.baofeng.blog.vo.front.FrontArticleVO.*;
import java.util.List;

public class ArticleConvert {

    /**
     * ArticleVO 、ArticleDetailResponse转换类
     * @param ArticleVO
     * @return ArticleDetailResponse
     */
    public static ArticleDetailResponse convertToDetailResponse(ArticleVO articleVO) {
        if (articleVO == null) return null;

        ArticleDetailResponse articleDetailResponse = new ArticleDetailResponse();

        articleDetailResponse.setId(articleVO.getId());
        articleDetailResponse.setAuthorName(
            articleVO.getAuthor() != null ? articleVO.getAuthor().getAuthorName() : null
        );
        articleDetailResponse.setType(articleVO.getType());
        articleDetailResponse.setOriginUrl(articleVO.getOriginUrl());
        articleDetailResponse.setThumbsUpTimes(articleVO.getThumbsUpTimes());
        articleDetailResponse.setAuthorId(
            articleVO.getAuthor() != null ? articleVO.getAuthor().getAuthorId() : null
        );
        articleDetailResponse.setArticleContent(articleVO.getArticleContent());
        articleDetailResponse.setArticleCover(articleVO.getArticleCover());
        articleDetailResponse.setArticleTitle(articleVO.getArticleTitle());
        articleDetailResponse.setViewTimes(articleVO.getViewTimes());
        articleDetailResponse.setCreatedAt(articleVO.getCreatedAt());
        articleDetailResponse.setUpdatedAt(articleVO.getUpdatedAt());

        List<String> categoryList = articleVO.getCategoryNameList();
        articleDetailResponse.setCategoryNameList(categoryList != null ? categoryList : List.of());

        List<String> tagList = articleVO.getTagNameList();
        articleDetailResponse.setTagNameList(tagList != null ? tagList : List.of());

        return articleDetailResponse;
    }

    /**
     * ArticleVO 、ArticleDetailResponse批量转换类
     * @param List<ArticleVO>
     * @return List<ArticleDetailResponse>
     */
    public static List<ArticleDetailResponse> convertToDetailResponseList(List<ArticleVO> list) {
        if (list == null || list.isEmpty()) return List.of();
        return list.stream()
               .map(ArticleConvert::convertToDetailResponse)
               .toList();
}
}

