package com.baofeng.blog.controller.front;

import com.baofeng.blog.service.FrontTagService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontTagVO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/front/tags")
@RequiredArgsConstructor
public class FrontTagController {
    
    private final FrontTagService frontTagService;

    /**
     * 获取所有标签
     * @return 标签列表
     */
    @GetMapping
    public ApiResponse<Object> getAllTags() {
        try {
            return ApiResponse.success(frontTagService.getAllTags());
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
            return ApiResponse.success(frontTagService.getHotTags(limit));
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
    public ApiResponse<Object> getTagDetail(@PathVariable Long id) {
        try {
            FrontTagVO.TagDetailVO tag = frontTagService.getTagDetail(id);
            if (tag == null) {
                return ApiResponse.error(404, "标签不存在");
            }
            return ApiResponse.success(tag);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取标签详情失败：" + e.getMessage());
        }
    }
} 