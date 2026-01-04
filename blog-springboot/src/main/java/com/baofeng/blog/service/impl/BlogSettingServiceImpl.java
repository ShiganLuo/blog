package com.baofeng.blog.service.impl;


import com.baofeng.blog.mapper.BlogSettingMapper;
import com.baofeng.blog.mapper.ArticleMapper;
import com.baofeng.blog.mapper.TagMapper;
import com.baofeng.blog.mapper.UserMapper;
import com.baofeng.blog.mapper.CategoryMapper;
import com.baofeng.blog.common.util.UrlNormalizeUtil;
import com.baofeng.blog.dto.ApiResponse;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.AdminConfigDetailResponse;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.SystemSettingDict;
import com.baofeng.blog.dto.admin.AdminBlogSettingDTO.SystemSettingDictResponse;
import com.baofeng.blog.dto.front.FrontBlogSettinDTO.*;
import com.baofeng.blog.entity.BlogSetting;
import com.baofeng.blog.service.BlogSettingService;
import com.baofeng.blog.enums.*;
import com.baofeng.blog.service.redis.RedisVisitCounter;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BlogSettingServiceImpl implements BlogSettingService {

    private final BlogSettingMapper blogSettingMapper;
    private final ArticleMapper articleMapper;
    private final TagMapper tagMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final RedisVisitCounter redisVisitCounter;
    private static final Logger logger = LoggerFactory.getLogger(BlogSettingService.class);

    public BlogSettingServiceImpl (
        BlogSettingMapper blogSettingMapper,
        ArticleMapper articleMapper,
        TagMapper tagMapper,
        CategoryMapper categoryMapper,
        UserMapper userMapper,
        RedisVisitCounter redisVisitCounter
    ) {
        this.blogSettingMapper = blogSettingMapper;
        this.articleMapper = articleMapper;
        this.tagMapper = tagMapper;
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
        this.redisVisitCounter = redisVisitCounter;
    }
    @Override
    public ApiResponse<String> addViews(){
        redisVisitCounter.incrSiteVisit();
        return ApiResponse.success("访问量增加成功");    
    }
    @Override
    public ApiResponse<String> initSetting(BlogSetting blogSetting){
        BlogSetting settring1 = blogSettingMapper.getSettingById(1L);
        if ( settring1 != null) {
            return ApiResponse.error(ResultCodeEnum.BAD_REQUEST,"博客系统设置已存在"); // 默认第一条记录为博客系统设置
        }
        
        int success = blogSettingMapper.insertSetting(blogSetting);
        return success > 0
            ? ApiResponse.success("网站初始化成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "网站初始化失败");

    }

    @Override
    public ApiResponse<String> updateSetting(BlogSetting blogSetting) {
        blogSetting.setId(1L);
        BlogSetting blogsetting2 = blogSettingMapper.getSettingById(1L);
        int success = 0;
        blogSetting.setFrontHeadBackground(
                UrlNormalizeUtil.stripUrlPrefix(blogSetting.getFrontHeadBackground())
        );
        blogSetting.setLogo(
                UrlNormalizeUtil.stripUrlPrefix(blogSetting.getLogo())
        );
        blogSetting.setFavicon(
                UrlNormalizeUtil.stripUrlPrefix(blogSetting.getFavicon())
        );
        blogSetting.setAuthorAvatar(
                UrlNormalizeUtil.stripUrlPrefix(blogSetting.getAuthorAvatar())
        );
        blogSetting.setUserAvatar(
                UrlNormalizeUtil.stripUrlPrefix(blogSetting.getUserAvatar())
        );
        blogSetting.setTouristAvatar(
                UrlNormalizeUtil.stripUrlPrefix(blogSetting.getTouristAvatar())
        );

        if (blogsetting2 == null) {
            blogSetting.setVisitCount(0L);
            success = blogSettingMapper.insertSetting(blogSetting);
        } else {
            success = blogSettingMapper.updateSettingById(blogSetting);
        }
        return success > 0
            ? ApiResponse.success("网站设置更新成功")
            : ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "网站设置更新失败");
    }

    @Override
    public ApiResponse<FrontConfigDetailResponse> getSettingByIdFront(Long id) {
        BlogSetting blogSetting = blogSettingMapper.getSettingById(id);
        Long articleCount = articleMapper.countAllArticles();
        Long tagCount = tagMapper.countAllTags();
        Long categoryCount = categoryMapper.countAllCategories();
        Long userCount = userMapper.countAllUsers();
        FrontConfigDetailResponse detail = new FrontConfigDetailResponse();
        detail.setWebsiteTitle(blogSetting.getWebsiteTitle());
        detail.setAuthorAvatar(blogSetting.getAuthorAvatar());
        detail.setLogo(blogSetting.getLogo());
        detail.setBlog_intro(blogSetting.getWebsiteIntro());
        detail.setFrontHeadBackground(blogSetting.getFrontHeadBackground());
        detail.setNotice(blogSetting.getNotice());
        detail.setPersonal_say(blogSetting.getAuthorPersonalSay());
        detail.setGitee(blogSetting.getGitee());
        detail.setBilibili(blogSetting.getBilibili());
        detail.setGithub(blogSetting.getGithub());
        detail.setQq_group(blogSetting.getQqGroup());
        detail.setQq(blogSetting.getQq());
        detail.setWe_chat_group(blogSetting.getWechatGroup());
        detail.setWeChat(blogSetting.getWechat());
        detail.setAli_pay(blogSetting.getAlipayQrCode());
        detail.setArticleCount(articleCount);
        detail.setWe_chat_pay(blogSetting.getWechatQrCode());
        detail.setView_time(0L);
        detail.setCreatedAt(blogSetting.getCreatedAt());
        detail.setTagCount(tagCount);
        detail.setCategoryCount(categoryCount);
        detail.setUserCount(userCount);
        return ApiResponse.success(detail);
    }
    
    @Override
    public ApiResponse<AdminConfigDetailResponse> getSettingByIdAdmin(Long id) {
        BlogSetting blogSetting = blogSettingMapper.getSettingById(id);
        AdminConfigDetailResponse detail = new AdminConfigDetailResponse();
        detail.setWebsiteChineseName(blogSetting.getWebsiteChineseName());
        detail.setWebsiteEnglishName(blogSetting.getWebsiteEnglishName());
        detail.setWebsiteTitle(blogSetting.getWebsiteTitle());
        detail.setWebsiteCreateTime(blogSetting.getWebsiteCreateTime());
        detail.setWebsiteIntro(blogSetting.getWebsiteIntro());

        detail.setLogo(blogSetting.getLogo());
        detail.setNotice(blogSetting.getNotice());
        detail.setFavicon(blogSetting.getFavicon());
        detail.setFrontHeadBackground(blogSetting.getFrontHeadBackground());
        detail.setIcpFilingNumber(blogSetting.getIcpFilingNumber());
        detail.setPsbFilingNumber(blogSetting.getPsbFilingNumber());

        detail.setAuthor(blogSetting.getAuthor());
        detail.setAuthorAvatar(blogSetting.getAuthorAvatar());
        detail.setAuthorIntro(blogSetting.getAuthorIntro());
        detail.setAuthroPersonalSay(blogSetting.getAuthorPersonalSay());
        detail.setUserAvatar(blogSetting.getUserAvatar());
        detail.setTouristAvatar(blogSetting.getTouristAvatar());

        detail.setGithub(blogSetting.getGithub());
        detail.setGitee(blogSetting.getGitee());
        detail.setBilibili(blogSetting.getBilibili());
        detail.setQq(blogSetting.getQq());
        detail.setQqGroup(blogSetting.getQqGroup());
        detail.setWechat(blogSetting.getWechat());
        detail.setWechatGroup(blogSetting.getWechatGroup());
        detail.setWeibo(blogSetting.getWeibo());
        detail.setCsdn(blogSetting.getCsdn());
        detail.setZhihu(blogSetting.getZhihu());
        detail.setJuejin(blogSetting.getJuejin());
        detail.setTwitter(blogSetting.getTwitter());
        detail.setStackoverflow(blogSetting.getStackoverflow());

        detail.setIsCommentReview(blogSetting.getIsCommentReview());
        detail.setIsEmailNotice(blogSetting.getIsEmailNotice());
        detail.setAlipayQRCode(blogSetting.getAlipayQrCode());
        detail.setWeiXinQRCode(blogSetting.getWechatQrCode());
        return ApiResponse.success(detail);
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
            case "FriendLinkStatus":
                systemSettingDicts = Arrays.stream(FriendLinkStatusEnum.values())
                                        .map( friendLinkStatusEnum -> {
                                            SystemSettingDict dict = new SystemSettingDict();
                                            dict.setDictLabel(friendLinkStatusEnum.getName());
                                            dict.setDictValue(String.valueOf(friendLinkStatusEnum.getCode()));
                                            dict.setCssClass("FriendLinkStatus-type-tag");
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

    @Override
    public ApiResponse<SomeFrontInformation> getSomeFrontInformation() {
        SomeFrontInformation someFrontInformation = new SomeFrontInformation();
        BlogSetting blogSetting = blogSettingMapper.getSettingById(1L);
        someFrontInformation.setIcpFilingNumber(blogSetting.getIcpFilingNumber());
        someFrontInformation.setWebsiteChineseName(blogSetting.getWebsiteChineseName());
        someFrontInformation.setFavicon(blogSetting.getFavicon());
        return ApiResponse.success(someFrontInformation);
    }
} 