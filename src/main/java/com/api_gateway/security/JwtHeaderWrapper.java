package com.api_gateway.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class JwtHeaderWrapper extends HttpServletRequestWrapper {

    private final String userId;
    private final String roles;

    public JwtHeaderWrapper(HttpServletRequest request, String userId, String roles) {
        super(request);
        this.userId = userId;
        this.roles = roles;
    }

    @Override
    public String getHeader(String name) {
        if (name.equalsIgnoreCase("X-USER-ID")) {
            return userId;
        }
        if (name.equalsIgnoreCase("X-ROLES")) {
            return roles;
        }
        return super.getHeader(name);
    }
}