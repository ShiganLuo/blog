package com.baofeng.blog.controller.front;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.baofeng.blog.service.BlogSettingService;
import com.baofeng.blog.service.ArticleService;
import com.baofeng.blog.service.TagService;
import com.baofeng.blog.service.CategoryService;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.configDetail;
// import com.baofeng.blog.vo.front.FrontBlogSettinVO.updateSettingRequest;
import com.baofeng.blog.entity.BlogSetting;



@RestController
@RequestMapping("/api/front/settings")
@RequiredArgsConstructor
@Validated
public class FrontBlogSettingController {
    private final BlogSettingService blogSettingService;
    private final ArticleService articleService;
    private final TagService tagService;
    private final CategoryService categoryService;

    @PutMapping("/addView")
    public ApiResponse<String> addView(){
        try {
            boolean success = blogSettingService.addViews();
            if ( success ){
                return ApiResponse.success("访问量增加成功");
            } else {
                return ApiResponse.error(400, "访问量增加失败");
            }
        } catch (Exception e){
            return ApiResponse.error(400, "增加失败" + e.getMessage());
        }
    }


    @GetMapping("/getBlogConfig")
    public ApiResponse<configDetail> getBlogConfig() {
        try {
            configDetail detail = new configDetail();

            Long articleCount = articleService.countAllArticles();
            Long tagCount = tagService.countAllTags();
            Long categoryCount = categoryService.countAllCategories();

            BlogSetting blogSetting = blogSettingService.getSettingById((long) 1);
            
            detail.setBlog_name(blogSetting.getSiteTitle());
            detail.setBlog_avatar(blogSetting.getSiteDescription());
            detail.setAvatar_bg(blogSetting.getSiteLogo());
            detail.setBlog_notice(blogSetting.getBlogNotice());
            detail.setPersonal_say(blogSetting.getPersonalSay());
            detail.setGit_ee_link(blogSetting.getGiteeLink());
            detail.setBilibili_link(blogSetting.getBilibiliLink());
            detail.setGithub_link(blogSetting.getGithubLink());
            detail.setQq_group(blogSetting.getQqGroup());
            detail.setQq_link(blogSetting.getQqLink());
            detail.setWe_chat_group(blogSetting.getWechatGroup());
            detail.setWe_chat_link(blogSetting.getWechatLink());
            detail.setAli_pay(blogSetting.getAliPay());
            detail.setArticleCount(articleCount);
            detail.setWe_chat_pay(blogSetting.getWechatPay());
            detail.setView_time(blogSetting.getVisitCount());
            detail.setCreatedAt(blogSetting.getCreatedAt());
            
            
            detail.setTagCount(tagCount);
            detail.setCategoryCount(categoryCount);

            // boolean success = blogSettingService.getBlogConfig();

            return ApiResponse.success(detail);

        } catch (Exception e) {
            return ApiResponse.error(400, "somewhere wrong" + e.getMessage());
        }
    }
}
