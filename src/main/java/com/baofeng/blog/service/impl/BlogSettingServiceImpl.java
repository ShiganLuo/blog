package com.baofeng.blog.service.impl;


import com.baofeng.blog.mapper.BlogSettingMapper;
import com.baofeng.blog.mapper.ArticleMapper;
import com.baofeng.blog.mapper.TagMapper;
import com.baofeng.blog.mapper.CategoryMapper;
import com.baofeng.blog.entity.BlogSetting;
import com.baofeng.blog.service.BlogSettingService;
import com.baofeng.blog.util.ResultCode;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminBlogSettingVO.initSettingRequest;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.configDetail;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.updateSettingRequest;


import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BlogSettingServiceImpl implements BlogSettingService {
    private final BlogSettingMapper blogSettingMapper;
    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public ApiResponse<String> addViews(){
        //id默认为1,指第一次初始化
        int success = blogSettingMapper.incrementVisitCount(1);
        return success > 0
            ? ApiResponse.success("访问量增加成功")
            : ApiResponse.error(400, "访问量增加失败");
    }
    @Override
    public ApiResponse<String> initSetting(initSettingRequest request){
        BlogSetting settring1 = blogSettingMapper.getSettingById((long) 1);
        if ( settring1 != null) {
            return ApiResponse.error(ResultCode.PARAM_ERROR,"博客系统设置已存在"); // 默认第一条记录为博客系统设置
        }
        BlogSetting setting = new BlogSetting();
        setting.setSiteTitle(null == request.siteTitle() ? "" : request.siteTitle());
        setting.setSiteDescription(null == request.siteDescription() ? "" : request.siteDescription());
        setting.setSiteLogo(null == request.siteLogo() ? "" : request.siteLogo());
        setting.setBlogNotice(null == request.blogNotice() ? "" : request.blogNotice());
        setting.setPersonalSay(null == request.personalSay() ? "" : request.personalSay());
        setting.setGiteeLink(null == request.giteeLink() ? "" : request.giteeLink());
        setting.setBilibiliLink(null == request.bilibiliLink() ? "" : request.bilibiliLink());
        setting.setGithubLink(null == request.githubLink() ? "" : request.githubLink());
        setting.setQqGroup(null == request.qqGroup() ? "" : request.qqGroup());
        setting.setQqLink(null == request.qqLink() ? "" : request.qqLink());
        setting.setWechatGroup(null == request.wechatGroup() ? "" : request.wechatGroup());
        setting.setWechatLink(null == request.wechatLink() ? "" : request.wechatLink());
        setting.setAliPay(null == request.aliPay() ? "" : request.aliPay());
        setting.setWechatPay(null == request.wechatPay() ? "" : request.wechatPay());
        //默认开启评论
        setting.setVisitCount(0);
        setting.setEnableComments(false);
        int success = blogSettingMapper.insertSetting(setting);
        return success > 0
            ? ApiResponse.success("网站初始化成功")
            : ApiResponse.error(ResultCode.SERVER_ERROR, "网站初始化失败");

    }

    @Override
    public ApiResponse<String> updateSettingById(initSettingRequest request) {
        BlogSetting setting = new BlogSetting();
        setting.setId((long) 1);// 默认第一条记录为博客系统设置
        setting.setSiteTitle(null == request.siteTitle() ? "" : request.siteTitle());
        setting.setSiteDescription(null == request.siteDescription() ? "" : request.siteDescription());
        setting.setSiteLogo(null == request.siteLogo() ? "" : request.siteLogo());
        setting.setBlogNotice(null == request.blogNotice() ? "" : request.blogNotice());
        setting.setPersonalSay(null == request.personalSay() ? "" : request.personalSay());
        setting.setGiteeLink(null == request.giteeLink() ? "" : request.giteeLink());
        setting.setBilibiliLink(null == request.bilibiliLink() ? "" : request.bilibiliLink());
        setting.setGithubLink(null == request.githubLink() ? "" : request.githubLink());
        setting.setQqGroup(null == request.qqGroup() ? "" : request.qqGroup());
        setting.setQqLink(null == request.qqLink() ? "" : request.qqLink());
        setting.setWechatGroup(null == request.wechatGroup() ? "" : request.wechatGroup());
        setting.setWechatLink(null == request.wechatLink() ? "" : request.wechatLink());
        setting.setAliPay(null == request.aliPay() ? "" : request.aliPay());
        setting.setWechatPay(null == request.wechatPay() ? "" : request.wechatPay());
        //默认不开启评论
        setting.setEnableComments(request.enableComments());
        setting.setVisitCount(0);
        int success = blogSettingMapper.updateSettingById(setting);
        return success > 0
            ? ApiResponse.success("网站设置更新成功")
            : ApiResponse.error(ResultCode.SERVER_ERROR, "网站设置更新失败");
    }

    @Override
    public ApiResponse<configDetail> getSettingById(Long id) {
        BlogSetting blogSetting = blogSettingMapper.getSettingById(id);
        Long articleCount = articleMapper.countAllArticles();
        Long tagCount = tagMapper.countAllTags();
        Long categoryCount = categoryMapper.countAllCategories();

        configDetail detail = new configDetail();
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

        return ApiResponse.success(detail);
    }
    
} 