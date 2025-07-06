package com.baofeng.blog.service;
import com.baofeng.blog.vo.admin.AdminBlogSettingVO.initSettingRequest;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.updateSettingRequest;

public interface BlogSettingService {

    boolean addViews();
    boolean initSetting(initSettingRequest request);
    boolean updateSettingById(updateSettingRequest request);


} 