package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.common.CategoryDTO.CategoryDictionaryResponse;
import com.baofeng.blog.service.CategoryService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/front/categories")
public class FrontCategoryController {

    private final CategoryService categoryService;

    public FrontCategoryController (
        CategoryService categoryService
    ) {
        this.categoryService = categoryService;
    }

    @GetMapping("/getAllcategories")
    public ApiResponse<List<CategoryDictionaryResponse>> getAllcategories(){
        return categoryService.getCategoryDictionary(null);
    }
    

    
}
