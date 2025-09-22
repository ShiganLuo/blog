package com.baofeng.blog.vo.admin;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonInclude;
public class AdminMockVO {
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)//只序列化非null值
    public static class routeResponse{
        
        @JsonUnwrapped
        private parent pa;

        private List<child> children;
    }
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class parent {
        //路由路径
        private String path;
        //路由名称（必须保持唯一）
        private String name;
        //路由重定向（默认跳转地址）
        private String redirect;
        private parentMeta meta;
        private List<child> children; // 允许父级有多个子路由

    }
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class parentMeta {
        // 菜单名称（兼容国际化、非国际化，如果用国际化的写法就必须在根目录的locales文件夹下对应添加）
        private String title;
        // 菜单图标
        private String icon;
        // 是否在菜单中显示
        private boolean showlink;
        // 菜单排序，值越高，排的越后（只针对顶级路由）
        private int rank;
        
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class child {
        // 路由路径
        private String path;
        // 路由名称（必须唯一并且和当前路由component字段对应的页面里用defineOptions包起来的name保持一致）
        private String name;
        //路由重定向
        private String redirect;
        //按需加载需要展示的画面
        private String component;
        //meta
        private childMeta meta;
        private List<child> children; // 允许子级也有子路由
    }
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class childMeta {
        // 菜单名称（兼容国际化、非国际化，如果用国际化的写法就必须在根目录的locales文件夹下对应添加）
        private String title;
        // 菜单图标
        private String icon;
        // 菜单名称右侧的额外图标
        private String extraIcon;
        // 是否显示该菜单
        private boolean showLink;
        // 是否显示父级菜单
        private boolean showParent;
        // 页面级别权限设置
        private List<String> roles;
        // 按钮级别权限设置
        private List<String> auths;
        // 是否缓存该路由页面（开启后，会保存该页面的整体状态，刷新后会清空状态）
        private boolean keepAlive;
        // 需要内嵌的iframe链接地址
        private String frameSrc;
        // 内嵌的iframe页面是否开启首次加载动画
        private boolean frameLoading;
        // 当前菜单名称或自定义信息禁止添加到标签页
        private childTransition transition;
        private boolean hiddenTag;
        // 显示在标签页的最大数量，需满足后面的条件：不显示在菜单中的路由并且是通过query或params传参模式打开的页面。在完整版全局搜dynamicLevel即可查看代码演示
        private long dynamicLevel;
        // 将某个菜单激活（主要用于通过query或params传参的路由，当它们通过配置showLink: false后不在菜单中显示，就不会有任何菜单高亮，而通过设置activePath指定激活菜单即可获得高亮，activePath为指定激活菜单的path）
        private String activePath;
    }
    // 页面加载动画（两种模式，第二种权重更高，第一种直接采用vue内置的transitions动画，第二种是使用animate.css编写进、离场动画，平台更推荐使用第二种模式，已经内置了animate.css，直接写对应的动画名即可）
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class  childTransition{
        // 当前页面动画，这里是第一种模式，比如 name: "fade" 更具体看后面链接 https://cn.vuejs.org/api/built-in-components.html#transition
        private String name;
        // 当前页面进场动画，这里是第二种模式，比如 enterTransition: "animate__fadeInLeft"
        private String enterTransition;
        // 当前页面离场动画，这里是第二种模式，比如 leaveTransition: "animate__fadeOutRight"
        private String leaveTransition;
    }

    @Data
    public static class loginFunctionEnabledResponse{
        private boolean sliderEnabled; //是否启用滑动验证码
        private boolean forgetPasswordEnabled; //是否启用忘记密码功能
        private boolean registerUserEnabled; //是否启用注册用户功能
    }
}
