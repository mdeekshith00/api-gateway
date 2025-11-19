package com.api_gateway.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {


	  @Override
	    public void commence(
	            HttpServletRequest request,
	            HttpServletResponse response,
	            AuthenticationException ex
	    ) throws IOException {

	        Object businessEx = request.getAttribute("app-exception");

	        // 👇 If request contains BusinessException details, let Global Handler manage it
	        if (businessEx != null) {
	            throw (RuntimeException) businessEx;
	        }

	        // ✅ Only write error when real authentication failure
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        response.setContentType("application/json");
	        response.getWriter().write(
	                "{\"error\": \"Unauthorized access\"}"
	        );
	    }

}
