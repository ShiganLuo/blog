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
        articleDetailResponse.setOrigin_url(articleVO.getOriginUrl());
        articleDetailResponse.setThumbs_up_times(articleVO.getThumbs_up_times());
        articleDetailResponse.setAuthor_id(
            articleVO.getAuthor() != null ? articleVO.getAuthor().getAuthorId() : null
        );
        articleDetailResponse.setArticle_content(articleVO.getArticle_content());
        articleDetailResponse.setArticle_cover(articleVO.getArticle_cover());
        articleDetailResponse.setArticle_title(articleVO.getArticle_title());
        articleDetailResponse.setView_times(articleVO.getView_times());
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

