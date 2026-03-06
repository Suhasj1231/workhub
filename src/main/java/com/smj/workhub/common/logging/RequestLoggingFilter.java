package com.smj.workhub.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();

        String fullUrl = query == null ? uri : uri + "?" + query;

        log.info("Incoming Request: {} {}", method, fullUrl);

        filterChain.doFilter(request, response);

        long duration = System.currentTimeMillis() - startTime;

        int status = response.getStatus();

        log.info("Completed: {} {} -> {} ({} ms)",
                method,
                fullUrl,
                status,
                duration
        );
    }
}