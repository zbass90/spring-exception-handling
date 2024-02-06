package com.zbass.exception.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Slf4j
public class AppHandlerExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Exception이 오면 ModelAndView로 반환
        if(ex instanceof IllegalArgumentException){
            try {
                log.info("IllegalArgumentException resolver to 400");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                return new ModelAndView(); // 뷰를 렌더링 하지 않고 정상 흐름으로 서블릿이 리턴됨
            } catch (IOException e) {
                log.error("resolver ex", e);
            }
        }
        return null;
    }
}
