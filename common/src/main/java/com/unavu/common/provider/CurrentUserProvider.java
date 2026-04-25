package com.unavu.common.provider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final HttpServletRequest request;

    public String getCurrentUserId() {
        return request.getHeader("X-User-Id");
    }
    public String getCurrentUserName() {
        return request.getHeader("X-Username");
    }
}