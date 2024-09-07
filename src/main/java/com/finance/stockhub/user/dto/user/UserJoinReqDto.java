package com.finance.stockhub.user.dto.user;

import com.finance.stockhub.user.entity.User;
import com.finance.stockhub.user.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserJoinReqDto {

    @NotBlank
    @Pattern(regexp = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$")
    private String password;

    @NotBlank
    private String name;

    private UserRole role;

    private int authCode;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .role(UserRole.USER)
                .build();
    }
}
