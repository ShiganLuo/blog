package com.baofeng.blog.common.util;

import java.util.*;
import java.util.stream.Collectors;

import com.baofeng.blog.dto.front.FrontCommentDTO.FrontArticleCommentResponse;
import com.baofeng.blog.dto.front.FrontCommentDTO.FrontCommentResponse;


public class CommentConvertUtil {

    public static List<FrontCommentResponse> buildCommentTree(List<FrontArticleCommentResponse> articleComments) {
        if (articleComments == null || articleComments.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 转换为 CommentResponse
        Map<Long, FrontCommentResponse> idToComment = articleComments.stream()
                .map(CommentConvertUtil::toCommentResponse)
                .collect(Collectors.toMap(FrontCommentResponse::getId, c -> c));

        // 2. 结果集合（存放根评论）
        List<FrontCommentResponse> roots = new ArrayList<>();

        // 3. 遍历，组装树
        for (FrontArticleCommentResponse  a : articleComments) {
            FrontCommentResponse current = idToComment.get(a.getId());

            if ("post".equalsIgnoreCase(a.getType())) {
                // 一级评论，直接放到根
                roots.add(current);
            } else if ("comment".equalsIgnoreCase(a.getType())) {
                // 子评论，找到它的父节点
                FrontCommentResponse parent = idToComment.get(a.getForId());
                if (parent != null) {
                    if (parent.getChildComments() == null) {
                        parent.setChildComments(new ArrayList<>());
                    }
                    parent.getChildComments().add(current);
                }
            }
        }

        return roots;
    }

    private static FrontCommentResponse toCommentResponse(FrontArticleCommentResponse  a) {
        FrontCommentResponse c = new FrontCommentResponse();
        c.setId(a.getId());
        c.setUserId(a.getUserId());
        c.setUserName(a.getUserName());
        c.setUserAvatar(a.getUserAvatar());
        c.setReplyUserName(a.getReplyUserName());
        c.setForId(a.getForId());
        c.setContent(a.getContent());
        c.setCreatedAt(a.getCreatedAt());
        c.setLikes(a.getLikes());
        c.setIpAdress(a.getIpAdress());
        c.setChildComments(new ArrayList<>());
        c.setIsLiked(a.getIsLiked());
        return c;
    }
}
