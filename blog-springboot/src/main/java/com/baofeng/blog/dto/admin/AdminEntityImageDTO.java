package com.baofeng.blog.dto.admin;
import lombok.Data;


public class AdminEntityImageDTO {

    @Data
    public static class UpdateImageIdEntity {
        private Long imageId;
        private String entityType;
        private Long entityId;
        private String usageType;
        private Integer sortOrder;
    }
}
