package com.baofeng.blog.util;

import java.util.*;
import java.util.stream.Collectors;

import com.baofeng.blog.dto.front.FrontCommentDTO.*;

public class CommentConvertUtil {

    public static List<CommentResponse> buildCommentTree(List<ArticleCommentResponse> articleComments) {
        if (articleComments == null || articleComments.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 转换为 CommentResponse
        Map<Long, CommentResponse> idToComment = articleComments.stream()
                .map(CommentConvertUtil::toCommentResponse)
                .collect(Collectors.toMap(CommentResponse::getId, c -> c));

        // 2. 结果集合（存放根评论）
        List<CommentResponse> roots = new ArrayList<>();

        // 3. 遍历，组装树
        for (ArticleCommentResponse a : articleComments) {
            CommentResponse current = idToComment.get(a.getId());

            if ("post".equalsIgnoreCase(a.getType())) {
                // 一级评论，直接放到根
                roots.add(current);
            } else if ("comment".equalsIgnoreCase(a.getType())) {
                // 子评论，找到它的父节点
                CommentResponse parent = idToComment.get(a.getFor_id());
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

    private static CommentResponse toCommentResponse(ArticleCommentResponse a) {
        CommentResponse c = new CommentResponse();
        c.setId(a.getId());
        c.setFrom_id(a.getFrom_id());
        c.setFrom_name(a.getFrom_name());
        c.setFrom_avatar(a.getFrom_avatar());
        c.setTo_name(a.getTo_name());
        c.setFor_id(a.getFor_id());
        c.setContent(a.getContent());
        c.setCreatedAt(a.getCreatedAt());
        c.setThumbs_up(a.getThumbs_up());
        c.setIpAdress(a.getIpAdress());
        c.setChildComments(new ArrayList<>());
        c.setIs_like(a.getIs_like());
        return c;
    }
}
