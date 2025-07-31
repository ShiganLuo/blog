package com.baofeng.blog.service;
import com.baofeng.blog.vo.admin.AdminBlogSettingVO.initSettingRequest;
import com.baofeng.blog.entity.BlogSetting;
public interface BlogSettingService {

    boolean addViews();
    boolean initSetting(initSettingRequest request);
    boolean updateSettingById(initSettingRequest request);
    BlogSetting getSettingById(Long id);

} 