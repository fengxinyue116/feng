package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j

public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        //直接放行的页面
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/commom/**",
                "/user/sendMsg",
                "/user/login"
        };
        //2.判断是否需要处理
        boolean check_id = check(urls, requestURI);
        if (check_id) {
            //放行
            log.info("不需要处理");
            filterChain.doFilter(request, response);
            return;
        }
        if (request.getSession().getAttribute("employee") != null) {
                log.info("用户已登录");
                Long empId= (Long) request.getSession().getAttribute("employee");
                BaseContext.setCurrentId(empId);
                filterChain.doFilter(request, response);
                return;
            }



        if (request.getSession().getAttribute("user") != null) {
        log.info("用户已登录");
        Long userId= (Long) request.getSession().getAttribute("user");
        BaseContext.setCurrentId(userId);
        filterChain.doFilter(request, response);
        return;
        }
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;}


    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    };

}
