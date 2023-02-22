package com.healthedge.payor.core.adaptor.interceptor;

import com.healthedge.payor.core.adaptor.context.SharedApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        SharedApplicationContext.init();
        Map<String,String> headers = SharedApplicationContext.getHeaders();
        String authTokenName = "Authorization";
        if(Objects.nonNull(request.getHeader(authTokenName))){
            headers.put(authTokenName,request.getHeader(authTokenName));
        }
        log.info("Pre Handle method is Calling");
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        SharedApplicationContext.close();
        log.info("post Handle method is Calling");

    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object
                    handler, Exception exception) {
        SharedApplicationContext.unload();
    }

}