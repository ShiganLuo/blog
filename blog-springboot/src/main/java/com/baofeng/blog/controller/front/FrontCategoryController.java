package com.baofeng.blog.controller.front;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import java.util.List;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.common.CategoryDTO.CategoryDictionaryResponse;
import com.baofeng.blog.service.CategoryService;
@RestController
@RequestMapping("/api/front/categories")
@RequiredArgsConstructor
@Validated
public class FrontCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/getAllcategories")
    public ApiResponse<List<CategoryDictionaryResponse>> getAllcategories(){
        return categoryService.getCategoryDictionary(null);
    }
    

    
}
