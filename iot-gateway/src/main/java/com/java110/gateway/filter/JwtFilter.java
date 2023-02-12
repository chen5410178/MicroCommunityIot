package com.java110.gateway.filter;

import com.java110.bean.ResultVo;
import com.java110.core.constant.CommonConstant;
import com.java110.core.factory.AuthenticationFactory;
import com.java110.core.factory.LoggerFactory;
import com.java110.core.utils.RequestUtils;
import com.java110.core.utils.SessionTokenUtil;
import com.java110.core.utils.StringUtil;
import org.slf4j.Logger;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

/**
 * Created by wuxw on 2018/5/2.
 */
public class JwtFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private String[] excludedUris;

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        excludedUris = filterConfig.getInitParameter("excludedUri").split(",");
    }

    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        String uri = request.getServletPath();
        //如果是 不能过滤的地址选择跳过
        if (RequestUtils.isExcludedUri(excludedUris,uri)) {
            chain.doFilter(request, response);
            return;
        }
        String token = "";
        try {
            //获取token
            token = SessionTokenUtil.getToken(request);
            try {
                long tokenStartTime = new Date().getTime();
                Map<String, String> claims = AuthenticationFactory.verifyToken(token);
                logger.debug("校验token 耗时：{}", new Date().getTime() - tokenStartTime);
                request.setAttribute("claims", claims);

            } catch (Exception e) {
                //Invalid signature/claims
                logger.error("解析token 失败 ：", e);
                throw new IllegalArgumentException("您还没有登录，请先登录");
            }

            chain.doFilter(req, res);
        } catch (IllegalArgumentException e) {
            logger.error("业务处理失败", e);
            if ("POST".equals(request.getMethod())) {
                writeJson(response, ResultVo.error(ResultVo.MSG_UNAUTHORIZED, e.getMessage()).getBody(),
                        "UTF-8");
            } else {
                //response.sendRedirect("/flow/login");
                noLogin(request, response);
            }

        } catch (Exception e) {
            //response.sendRedirect("/flow/login");
            noLogin(request, response);
        }
    }

    public void noLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String type = request.getHeader("X-Requested-With");// XMLHttpRequest     HttpServletRequest -> request
        String unauthorizedUrl = request.getHeader("unauthorized-url");
        if (StringUtil.isEmpty(unauthorizedUrl)) {
            unauthorizedUrl = "/user.html#/pages/frame/login";
        }
        if ("XMLHttpRequest".equals(type)) {
            //是ajax请求
            // 异步请求下的重定向
            response.setHeader("CONTEXTPATH", unauthorizedUrl);//重定向目标地址
            response.setHeader("location", unauthorizedUrl);//重定向目标地址
            response.setStatus(401);
        } else {
            //非ajax请求，直接使用重定向
            response.sendRedirect(unauthorizedUrl);
        }
    }



    private void writeJson(HttpServletResponse response, String data, String encoding) {
        //设置编码格式
        response.setContentType("text/plain;charset=" + encoding);
        response.setCharacterEncoding(encoding);

        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
