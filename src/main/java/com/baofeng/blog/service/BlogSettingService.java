package com.baofeng.blog.service;
import com.baofeng.blog.vo.ApiResponse;
import com.baofeng.blog.vo.admin.AdminBlogSettingVO.initSettingRequest;
import com.baofeng.blog.vo.front.FrontBlogSettinVO.configDetail;
public interface BlogSettingService {

    /**
     * 增加网站访问量
     * @return
     */
    public ApiResponse<String> addViews();

    /**
     * 初始化网站设置
     * @param request
     * @return
     */
    public ApiResponse<String> initSetting(initSettingRequest request);

    /**
     * 更新网站设置
     * @param request
     * @return
     */
    public ApiResponse<String> updateSettingById(initSettingRequest request);

    /**
     * 获取网站设置
     * @param id
     * @return
     */
    public ApiResponse<configDetail> getSettingById(Long id);

} 