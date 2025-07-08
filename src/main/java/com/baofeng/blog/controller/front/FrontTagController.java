package com.baofeng.blog.controller.front;

import com.baofeng.blog.service.TagService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontTagVO.TagDictionaryResponse;
import com.baofeng.blog.entity.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/front/tags")
@RequiredArgsConstructor
public class FrontTagController {
    
    private final TagService tagService;

    /**
     * 获取所有标签
     * @return 标签列表
     */
    @GetMapping("/getAllTags")
    public ApiResponse<List<TagDictionaryResponse>> getAllTags() {
        try {

            List<Tag> tags = tagService.getTagDictionary();
            List<TagDictionaryResponse> tagDictionaryResponse = tags.stream()
            .map(tag -> {
                TagDictionaryResponse resp = new TagDictionaryResponse();
                resp.setId(tag.getId());
                resp.setTag_name(tag.getName());
                return resp;
            })
            .collect(Collectors.toList());

            return ApiResponse.success(tagDictionaryResponse);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取标签列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取热门标签
     * @param limit 限制数量（默认10）
     * @return 标签列表
     */
    @GetMapping("/hot")
    public ApiResponse<Object> getHotTags(@RequestParam(defaultValue = "10") int limit) {
        try {
            return ApiResponse.success(tagService.getHotTags(limit));
        } catch (Exception e) {
            return ApiResponse.error(500, "获取热门标签失败：" + e.getMessage());
        }
    }

    /**
     * 获取标签详情
     * @param id 标签ID
     * @return 标签详细信息
     */
    @GetMapping("/{id}")
    public ApiResponse<Tag> getTagDetail(@PathVariable Long id) {
        try {
            Tag tag = tagService.getTagDetail(id);
            if (tag == null) {
                return ApiResponse.error(404, "标签不存在");
            }
            return ApiResponse.success(tag);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取标签详情失败：" + e.getMessage());
        }
    }
} 