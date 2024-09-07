package com.finance.stockhub.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {


    // TODO : 로그인 유저 계정 정보를 createdBy, updatedBy 삽입
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.empty();
    }

}
