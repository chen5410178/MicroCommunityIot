package com.java110.gateway.aop;

import com.alibaba.fastjson.JSONObject;
import com.java110.core.constant.CommonConstant;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.StringUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;


/**
 * 主要用于登录后写token使用
 * Created by wuxw on 2018/5/2.
 */
@Aspect
@Component
public class PageProcessAspect {

    private static Logger logger = LoggerFactory.getLogger(PageProcessAspect.class);

    @Pointcut("execution(public * com.java110..*.*Controller.*(..)) ")
    public void dataProcess() {
    }

    /**
     * 初始化数据
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Before("dataProcess()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {

    }


    @AfterReturning(returning = "ret", pointcut = "dataProcess()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
    }

    //后置异常通知
    @AfterThrowing("dataProcess()")
    public void throwException(JoinPoint jp) {
    }

    //后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
    @After("dataProcess()")
    public void after(JoinPoint jp) throws IOException {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String token = request.getAttribute(CommonConstant.COOKIE_AUTH_TOKEN) != null ? request.getAttribute(CommonConstant.COOKIE_AUTH_TOKEN).toString() : null;
        //保存日志处理
        if (StringUtil.isEmpty(token)) {
            return;
        }
        //写cookies信息
        writeCookieInfo(token, attributes);

    }

    //环绕通知,环绕增强，相当于MethodInterceptor
    @Around("dataProcess()")
    public Object around(ProceedingJoinPoint pjp) {
        try {
            Object o = pjp.proceed();
            return o;
        } catch (Throwable e) {
            logger.error("执行方法异常", e);
            return new ResponseEntity(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 写cookie 信息
     *
     * @param token      页面封装信息
     * @param attributes
     * @throws IOException
     */
    private void writeCookieInfo(String token, ServletRequestAttributes attributes) throws IOException {
        HttpServletResponse response = attributes.getResponse();
        Cookie cookie = new Cookie(CommonConstant.COOKIE_AUTH_TOKEN, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.flushBuffer();
    }
}
