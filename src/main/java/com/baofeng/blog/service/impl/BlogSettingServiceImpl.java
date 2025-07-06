package com.baofeng.blog.service.impl;

import com.baofeng.blog.entity.BlogSetting;
import com.baofeng.blog.mapper.BlogSettingMapper;
import com.baofeng.blog.service.BlogSettingService;
import com.baofeng.blog.vo.admin.AdminBlogSettingVO.initSettingRequest;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.updateSettingRequest;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BlogSettingServiceImpl implements BlogSettingService {
    private final BlogSettingMapper blogSettingMapper;
    @Override
    public boolean addViews(){
        //id默认为1,指第一次初始化
        int success = blogSettingMapper.incrementVisitCount(1);
        if ( success > 0 ){
            return true;
        } else {
            return false;
        }
    }
    @Override
    public boolean initSetting(initSettingRequest request){
        BlogSetting setting = new BlogSetting();
        setting.setSiteTitle(null == request.siteTitle() ? "" : request.siteTitle());
        setting.setSiteDescription(null == request.siteDescription() ? "" : request.siteDescription());
        setting.setSiteLogo(null == request.siteLogo() ? "" : request.siteLogo());
        setting.setQqGroup(null == request.qqGroup() ? "" : request.qqGroup());
        setting.setWechatGroup(null == request.wechatGroup() ? "" : request.wechatGroup());
        setting.setAliPay(null == request.aliPay() ? "" : request.aliPay());
        setting.setWechatPay(null == request.wechatPay() ? "" : request.wechatPay());
        //默认开启评论
        setting.setEnableComments(request.enableComments());
        setting.setVisitCount(0);
        setting.setEnableComments(false);
        int success = blogSettingMapper.insertSetting(setting);
        if ( success > 0 ) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean updateSettingById(updateSettingRequest request) {
        BlogSetting setting = new BlogSetting();
        setting.setId(1L); // 假设我们只更新ID为1的设置
        setting.setSiteTitle(null == request.siteTitle() ? "" : request.siteTitle());
        setting.setSiteDescription(null == request.siteDescription() ? "" : request.siteDescription());
        setting.setSiteLogo(null == request.siteLogo() ? "" : request.siteLogo());
        setting.setQqGroup(null == request.qqGroup() ? "" : request.qqGroup());
        setting.setWechatGroup(null == request.wechatGroup() ? "" : request.wechatGroup());
        setting.setAliPay(null == request.aliPay() ? "" : request.aliPay());
        setting.setWechatPay(null == request.wechatPay() ? "" : request.wechatPay());
        setting.setEnableComments(request.enableComments());
        int success = blogSettingMapper.updateSettingById(setting);
        if ( success > 0 ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BlogSetting getSettingById(Long id) {
        BlogSetting blogSetting = blogSettingMapper.getSettingById(id);
        return blogSetting;
    }
    
} 