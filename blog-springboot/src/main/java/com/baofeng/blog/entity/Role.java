package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Role {
    private Long id;
    private String role_name;
    private String role_desc;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
