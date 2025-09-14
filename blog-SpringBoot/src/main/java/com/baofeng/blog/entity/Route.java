package com.baofeng.blog.entity;
import java.util.List;
import lombok.Data;

@Data
public class Route {
    private Integer id;
    private Integer parentId;
    private String path;
    private String name;
    private String redirect;
    private String component;

    /* meta信息 */
    private String title;
    private String icon;
    private String extraIcon;
    private Boolean showLink = true;
    private Boolean showParent = true;
    private Integer ranks;

    /* 权限相关 */
    private List<String> roles;
    private List<String> auths;

    /* 页面缓存和性能 */
    private Boolean keepAlive = false;

    /* iframe相关 */
    private String frameSrc;
    private Boolean frameLoading = false;

    /* 动画相关 */
    private String transitionName;
    private String enterTransition;
    private String leaveTransition;

    /* 标签页相关 */
    private Boolean hiddenTag = false;
    private Integer dynamicLevel;
    private String activePath;
    
}
