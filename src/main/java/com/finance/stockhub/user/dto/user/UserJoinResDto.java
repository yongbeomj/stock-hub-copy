package com.finance.stockhub.user.dto.user;

import com.finance.stockhub.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserJoinResDto {
    private Long id;
    private String email;
    private LocalDateTime createdAt;

    public UserJoinResDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
    }
}
