package com.practice.soapadaptor.interceptor;

import com.practice.soapadaptor.context.SharedApplicationContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle
            (HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        SharedApplicationContext.loadData();
        evaluateHeader(request);
        log.info("Pre Handle method is Calling");
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        log.info("post Handle method is Calling");

    }
    @Override
    public void afterCompletion
            (HttpServletRequest request, HttpServletResponse response, Object
                    handler, Exception exception) throws Exception {
        SharedApplicationContext.unload();
    }

    private void evaluateHeader(HttpServletRequest request) {
        Map<String,String> headers = SharedApplicationContext.getHeaders();
        String authTokenName = "authorization";
        if(Objects.nonNull(request.getHeader(authTokenName))){
            headers.put(authTokenName,request.getHeader(authTokenName));
        }
    }

}
