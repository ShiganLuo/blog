package com.baofeng.blog.controller.front;

import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.service.NoneTableService;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/front/utils")
public class FrontNoneTableController {

    private final NoneTableService noneTableService;
    
    public FrontNoneTableController (
        NoneTableService noneTableService
    )  {
        this.noneTableService = noneTableService;
    }
    @GetMapping("/oneSentence")
    public ApiResponse<String> getOneSentence() {
        return noneTableService.getOneSentence();

    }
    
    
}
