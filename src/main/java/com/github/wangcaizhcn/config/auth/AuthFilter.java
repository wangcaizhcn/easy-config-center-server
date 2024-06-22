package com.github.wangcaizhcn.config.auth;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter {

	@Value("${server.servlet.context-path:}")
    private String context;
	
	private static String[] uris = {
		"/login/",
		"/doc.html",
		".js",
		".css",
		".ico",
		"swagger-resources",
		"api-docs",
		".png",
		".woff",
		".ttf",
		"/management/configs"
	};
	
	private static final String UNAUTHORIZED_MESSAGE = "请登录!";
	
	protected String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return token;
    }
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String uri = request.getRequestURI();
		
		// 访问根目录不用授权
		if(uri.equals(context + "/")) {
			filterChain.doFilter(request, response);
            return;
		}
		
		// 白名单直接放行
		for(String u : uris) {
			if(uri.indexOf(u) > -1) {
				filterChain.doFilter(request, response);
	            return;
			}
		}
		
		// token不能为空
		String token = getToken(request);
		if(StringUtils.isBlank(token)) {
			response.sendError(HttpStatus.UNAUTHORIZED.value(), UNAUTHORIZED_MESSAGE);
			return;
		}
		
		// 验证token
		try {
			AuthService.checkToken(token);
		} catch(Exception e) {
			e.printStackTrace();
			response.sendError(HttpStatus.UNAUTHORIZED.value(), UNAUTHORIZED_MESSAGE);
			return;
		}
		
		filterChain.doFilter(request, response);
	}

}
