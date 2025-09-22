package com.baofeng.blog.entity;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class UserRole {
    private Long id;
    private Long user_id;
    private Long role_id;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
