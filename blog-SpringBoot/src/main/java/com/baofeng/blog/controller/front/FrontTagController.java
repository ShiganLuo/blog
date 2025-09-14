package com.baofeng.blog.controller.front;

import com.baofeng.blog.service.TagService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.common.Tag.TagDictionaryResponse;
import com.baofeng.blog.entity.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return tagService.getTagDictionary();
    }

    /**
     * 获取热门标签
     * @param limit 限制数量（默认10）
     * @return 标签列表
     */
    @GetMapping("/hot")
    public ApiResponse<List<String>> getHotTags(@RequestParam(defaultValue = "10") int limit) {
        return tagService.getHotTags(limit);
    }

    /**
     * 获取标签详情
     * @param id 标签ID
     * @return 标签详细信息
     */
    @GetMapping("/{id}")
    public ApiResponse<Tag> getTagDetail(@PathVariable Long id) {
        return tagService.getTagDetail(id);
    }
} 