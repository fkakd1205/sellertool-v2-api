package com.sellertool.server.config.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sellertool.server.config.exception.CsrfAccessDeniedException;
import com.sellertool.server.config.exception.CsrfExpiredJwtException;
import com.sellertool.server.config.exception.CsrfNullPointerException;
import com.sellertool.server.domain.message.model.dto.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CsrfExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("============CsrfExceptionFilter============");
        try{
            filterChain.doFilter(request, response);
        } catch (CsrfNullPointerException | CsrfAccessDeniedException | CsrfExpiredJwtException e){
            errorResponse(response, HttpStatus.FORBIDDEN, "invalid_csrf", e.getMessage());
        }
    }

    private void errorResponse(HttpServletResponse response, HttpStatus status, String resMessage, String resMemo) throws IOException, ServletException {
        Message message = new Message();

        message.setStatus(status);
        message.setMessage(resMessage);
        message.setMemo(resMemo);

        String msg = new ObjectMapper().writeValueAsString(message);
        response.setStatus(message.getStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(msg);
        response.getWriter().flush();
    }
}