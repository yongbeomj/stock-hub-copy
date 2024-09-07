package com.finance.stockhub.user.dto.user;

import com.finance.stockhub.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserJoinResDto {
    private Long id;
    private String email;
    private String name;

    public UserJoinResDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
    }
}
