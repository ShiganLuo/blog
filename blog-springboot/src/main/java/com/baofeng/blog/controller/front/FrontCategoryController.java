package com.baofeng.blog.controller.front;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import java.util.List;

import com.baofeng.blog.service.CategoryService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.common.Category.CategoryDictionaryResponse;
@RestController
@RequestMapping("/api/front/categories")
@RequiredArgsConstructor
@Validated
public class FrontCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/getAllcategories")
    public ApiResponse<List<CategoryDictionaryResponse>> getAllcategories(){
        return categoryService.getCategoryDictionary();
    }
    

    
}
