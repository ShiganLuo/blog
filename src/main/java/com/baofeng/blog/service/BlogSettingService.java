package com.baofeng.blog.service;
import com.baofeng.blog.vo.admin.AdminBlogSettingVO.initSettingRequest;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.updateSettingRequest;
import com.baofeng.blog.entity.BlogSetting;
public interface BlogSettingService {

    boolean addViews();
    boolean initSetting(initSettingRequest request);
    boolean updateSettingById(updateSettingRequest request);
    BlogSetting getSettingById(Long id);

} 