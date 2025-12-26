package com.baofeng.blog.service.impl;


import com.baofeng.blog.mapper.BlogSettingMapper;
import com.baofeng.blog.mapper.ArticleMapper;
import com.baofeng.blog.mapper.TagMapper;
import com.baofeng.blog.mapper.UserMapper;
import com.baofeng.blog.mapper.CategoryMapper;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.InitSettingRequest;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.SystemSettingDict;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.SystemSettingDictResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.*;
import com.baofeng.blog.entity.BlogSetting;
import com.baofeng.blog.service.BlogSettingService;
import com.baofeng.blog.entity.User;
import com.baofeng.blog.enums.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class BlogSettingServiceImpl implements BlogSettingService {
    private final BlogSettingMapper blogSettingMapper;
    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private static final Logger logger = LoggerFactory.getLogger(BlogSettingService.class);
    @Override
    public ApiResponse<String> addViews(){
        //id默认为1,指第一次初始化
        int success = blogSettingMapper.incrementVisitCount(1);
        return success > 0
            ? ApiResponse.success("访问量增加成功")
            : ApiResponse.error(400, "访问量增加失败");
    }
    @Override
    public ApiResponse<String> initSetting(InitSettingRequest request){
        BlogSetting settring1 = blogSettingMapper.getSettingById((long) 1);
        if ( settring1 != null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"博客系统设置已存在"); // 默认第一条记录为博客系统设置
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
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "网站初始化失败");

    }

    @Override
    public ApiResponse<String> updateSettingById(InitSettingRequest request) {
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
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "网站设置更新失败");
    }

    @Override
    public ApiResponse<ConfigDetail> getSettingById(Long id) {
        BlogSetting blogSetting = blogSettingMapper.getSettingById(id);
        Long articleCount = articleMapper.countAllArticles();
        Long tagCount = tagMapper.countAllTags();
        Long categoryCount = categoryMapper.countAllCategories();
        Long userCount = userMapper.countAllUsers();
        ConfigDetail detail = new ConfigDetail();
        detail.setWebsiteTitle(blogSetting.getSiteTitle());
        detail.setLogo(blogSetting.getSiteLogo());
        detail.setBlog_intro(blogSetting.getSiteDescription());
        detail.setFrontHeadBackground(blogSetting.getSiteLogo());
        detail.setNotice(blogSetting.getBlogNotice());
        detail.setPersonal_say(blogSetting.getPersonalSay());
        detail.setGitee(blogSetting.getGiteeLink());
        detail.setBilibili(blogSetting.getBilibiliLink());
        detail.setGithub(blogSetting.getGithubLink());
        detail.setQq_group(blogSetting.getQqGroup());
        detail.setQq(blogSetting.getQqLink());
        detail.setWe_chat_group(blogSetting.getWechatGroup());
        detail.setWeChat(blogSetting.getWechatLink());
        detail.setAli_pay(blogSetting.getAliPay());
        detail.setArticleCount(articleCount);
        detail.setWe_chat_pay(blogSetting.getWechatPay());
        detail.setView_time(blogSetting.getVisitCount());
        detail.setCreatedAt(blogSetting.getCreatedAt());
        
        detail.setTagCount(tagCount);
        detail.setCategoryCount(categoryCount);
        detail.setUserCount(userCount);
        return ApiResponse.success(detail);
    }
    
    @Override
    public ApiResponse<FriendLinkPackResponse> getAllFriendLink(FriendLinkRequest request) {
        int current = request.current() != null ? request.current() : 1;
        int size = request.size() != null ? request.size() : 10;
        PageHelper.startPage(current, size);
        List<FriendLinkResponse> list = blogSettingMapper.selectAllFriendLinks();
        PageInfo<FriendLinkResponse> pageInfo = new PageInfo<>(list);
        FriendLinkPackResponse response = new FriendLinkPackResponse();
        response.setTotal(pageInfo.getTotal());
        response.setList(pageInfo.getList());
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<String> addFriendLink(AddFriendLinkRequest addFriendLinkRequest) {
        int rowUpdated = blogSettingMapper.insertFriendLink(addFriendLinkRequest);
        User user = userMapper.selectUserById(addFriendLinkRequest.user_id());
        if (user == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"用户不存在, 用户必须登录才能添加友链");
        }
        return rowUpdated > 0 
            ? ApiResponse.success("友链添加成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"友链添加失败");

    }

    @Override
    public ApiResponse<String> updateFriendLink(AddFriendLinkRequest addFriendLinkRequest) {
        if (addFriendLinkRequest.id() == null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"更新友链, id不能为空");
        }
        if (addFriendLinkRequest.id() == 1) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED,"没有权限更新该友链");
        }
        BlogSetting blogSetting = blogSettingMapper.getSettingById(addFriendLinkRequest.id());
        if (blogSetting == null) {
            return ApiResponse.error(ResultCodeEnum.NOT_FOUND,"该友链不存在");
        }
        int rowUpdated = blogSettingMapper.updateFriendLinkById(addFriendLinkRequest);
        return rowUpdated > 0
            ? ApiResponse.success("友链更新成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"友链更新失败");
    }

    @Override
    public ApiResponse<String> deleteFriendLink(Long id) {
        if (id == (long) 1) {
            return ApiResponse.error(ResultCodeEnum.UNAUTHORIZED,"没有权限删除该友链");
        }
        int rowUpdated = blogSettingMapper.deleteSettingById(id);
        return rowUpdated > 0
            ? ApiResponse.success("友链删除成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR,"友链删除失败");
    }

    @Override
    public ApiResponse<SystemSettingDictResponse> getSystemSettingDict(String type) {
        List<SystemSettingDict> systemSettingDicts = new ArrayList<>();
        switch (type) {
            case "articleType":
                systemSettingDicts = Arrays.stream(ArticleTypeEnum.values())
                                    .map(articleTypeEnum -> {
                                        SystemSettingDict dict = new SystemSettingDict();
                                        dict.setDictLabel(articleTypeEnum.getType());
                                        dict.setDictValue(String.valueOf(articleTypeEnum.getCode()));
                                        dict.setCssClass("article-type-tag");
                                        dict.setListClass("info");
                                        return dict;
                                    })
                                    .collect(Collectors.toList());
                break;
            case "gender":
                systemSettingDicts = Arrays.stream(GenderEnum.values())
                            .map(genderEnum -> {
                                SystemSettingDict dict = new SystemSettingDict();
                                dict.setDictLabel(genderEnum.getGender());
                                dict.setDictValue(String.valueOf(genderEnum.getCode()));
                                dict.setCssClass("gender-type-tag");
                                dict.setListClass("info");
                                return dict;
                            })
                            .collect(Collectors.toList());
                break;
            case "role":
                systemSettingDicts = Arrays.stream(RoleTypeEnum.values())
                                        .map(roleTypeEnum -> {
                                            SystemSettingDict dict = new SystemSettingDict();
                                            dict.setDictLabel(roleTypeEnum.getDescription());
                                            dict.setDictValue(roleTypeEnum.getRole());
                                            dict.setCssClass("role-type-tag");
                                            dict.setListClass("info");
                                            return dict;
                                        })
                                        .collect(Collectors.toList());
                break;
            case "userStatus":
                systemSettingDicts = Arrays.stream(UserStatusEnum.values())
                                        .map(userStatusEnum -> {
                                            SystemSettingDict dict = new SystemSettingDict();
                                            dict.setDictLabel(userStatusEnum.getDescription());
                                            dict.setDictValue(userStatusEnum.getStatus());
                                            dict.setCssClass("userStatus-type-tag");
                                            dict.setListClass("info");
                                            return dict;
                                        })
                                        .collect(Collectors.toList());
                break;
            default:
                logger.info(type);
                return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"不支持的获取类型");
        }


        int total = systemSettingDicts.size();
        SystemSettingDictResponse systemSettingDictResponse = new SystemSettingDictResponse();
        systemSettingDictResponse.setList(systemSettingDicts);
        systemSettingDictResponse.setTotal(total);
        return ApiResponse.success(systemSettingDictResponse);
        
    }
} 