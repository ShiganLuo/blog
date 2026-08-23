package com.baofeng.blog.dto.admin;

import lombok.Data;
import java.util.List;

import com.baofeng.blog.common.annotation.MinioFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdminTalkDTO {

    /** 后台说说列表请求 */
    @Data
    public static class AdminTalkPageRequest {
        @NotNull(message = "当前页不能为空")
        @Min(value = 1, message = "当前页必须大于等于 1")
        private Integer current;

        @NotNull(message = "每页数量不能为空")
        @Min(value = 1, message = "每页数量必须大于等于 1")
        private Integer size;

        /** 筛选状态：空=全部，1=公开，2=私密 */
        private Integer status;
    }

    /** 后台说说列表响应 */
    @Data
    public static class AdminTalkPageResponse {
        private Long total;
        private List<AdminTalkResult> list;
    }

    /** 后台说说单条结果 */
    @Data
    public static class AdminTalkResult {
        private Long id;
        private String content;
        private String images;      // JSON 字符串，前端提交时用
        private Integer isTop;
        private Integer status;     // 1=公开, 2=私密
        private String createTime;
        @MinioFile
        private String avatar;
        private String nickname;
        private List<String> imgs;  // 解析后的图片数组
    }

    /** 新增/修改说说请求 */
    @Data
    public static class AdminTalkSaveRequest {
        private Long id;            // null=新增, 非null=修改
        @NotNull(message = "内容不能为空")
        private String content;
        private Integer isTop;
        private Integer status;
        private String images;      // JSON 字符串
    }

    /** 删除说说请求 */
    @Data
    public static class AdminTalkDeleteRequest {
        @NotNull(message = "ID列表不能为空")
        private List<Long> ids;
    }
}
