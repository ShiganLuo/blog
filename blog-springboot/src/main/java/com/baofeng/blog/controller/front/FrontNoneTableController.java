package com.baofeng.blog.controller.front;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.service.NoneTableService;

@RestController
@RequestMapping("/api/front/utils")
@RequiredArgsConstructor
@Validated
public class FrontNoneTableController {

    final NoneTableService noneTableService;
    
    @GetMapping("/oneSentence")
    public ApiResponse<String> getOneSentence() {
        return noneTableService.getOneSentence();

    }
    
    
}
