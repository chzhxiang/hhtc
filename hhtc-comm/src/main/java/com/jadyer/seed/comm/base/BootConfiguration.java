package com.jadyer.seed.comm.base;

import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.util.HttpUtil;
import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@ConfigurationProperties(prefix="auth")
public class BootConfiguration extends WebMvcConfigurerAdapter {
    @Value("${spring.async.corePoolSize}")
    private int corePoolSize;
    @Value("${spring.async.maxPoolSize}")
    private int maxPoolSize;
    @Value("${spring.async.queueCapacity}")
    private int queueCapacity;
    @Bean
    public Executor mppExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("MppExecutor-");
        executor.setCorePoolSize(this.corePoolSize);
        executor.setMaxPoolSize(this.maxPoolSize);
        executor.setQueueCapacity(this.queueCapacity);
        //队列满的时候，使用的拒绝策略
        //ABORT（缺省）  ：不执行，并抛TaskRejectedException异常
        //DISCARD       ：不执行，也不抛异常
        //DISCARD_OLDEST：丢弃queue中最旧的那个任务
        //CALLER_RUNS   ：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/login.jsp");
    }
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver irvr = new InternalResourceViewResolver();
        irvr.setPrefix("/WEB-INF/jsp/");
        irvr.setSuffix(".jsp");
        irvr.setViewClass(JstlView.class);
        registry.viewResolver(irvr);
    }


    private String unauthorizedUrl;
    private List<String> anonymousList = new ArrayList<>();
    public String getUnauthorizedUrl() {
        return unauthorizedUrl;
    }
    public void setUnauthorizedUrl(String unauthorizedUrl) {
        this.unauthorizedUrl = unauthorizedUrl;
    }
    public List<String> getAnonymousList() {
        return anonymousList;
    }
    @Bean
    public Filter authenticationFilter(){
        return new AuthFilter(this.unauthorizedUrl, this.anonymousList);
    }
    private static class AuthFilter extends OncePerRequestFilter {
        private String unauthorizedUrl;
        private List<String> anonymousList = new ArrayList<>();
        AuthFilter(String unauthorizedUrl, List<String> anonymousList){
            this.unauthorizedUrl = unauthorizedUrl;
            this.anonymousList = anonymousList;
            this.anonymousList.add("/shutdown");
        }
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            if(!request.getServletPath().startsWith("/weixin") && StringUtils.equals("0", LogUtil.getSuspend())){
                response.setCharacterEncoding(HttpUtil.DEFAULT_CHARSET);
                response.setContentType("text/plain; charset=" + HttpUtil.DEFAULT_CHARSET);
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
                PrintWriter out = response.getWriter();
                out.print("服务已挂起");
                out.flush();
                out.close();
                return;
            }
            boolean disallowAnonymousVisit = true;
            for(String anonymousResource : anonymousList){
                if(anonymousResource.equals(request.getServletPath())){
                    disallowAnonymousVisit = false;
                    break;
                }
                if(anonymousResource.endsWith("/**") && request.getServletPath().startsWith(anonymousResource.replace("/**", ""))){
                    disallowAnonymousVisit = false;
                    break;
                }
            }
            if(disallowAnonymousVisit && null==request.getSession().getAttribute(Constants.WEB_SESSION_USER)){
                response.sendRedirect(request.getContextPath() + this.unauthorizedUrl);
            }else{
                filterChain.doFilter(request, response);
            }
        }
    }


    @Bean
    public Filter menuFilter(){
        return new MenuFilter();
    }
    private static class MenuFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            request.getSession().setAttribute(Constants.WEB_CURRENT_MENU, "menu_sys");
            if(request.getServletPath().startsWith("/fans")){
                request.getSession().setAttribute(Constants.WEB_CURRENT_MENU, "menu_fans");
            }
            if(request.getServletPath().startsWith("/funds") || request.getServletPath().startsWith("/refund") || request.getServletPath().startsWith("/order")){
                request.getSession().setAttribute(Constants.WEB_CURRENT_MENU, "menu_funds");
            }
            if(request.getServletPath().startsWith("/mpp")){
                request.getSession().setAttribute(Constants.WEB_CURRENT_MENU, "menu_mpp");
            }
            if(request.getServletPath().startsWith("/view")){
                String url = request.getParameter("url");
                if(url.startsWith("fans")){
                    request.getSession().setAttribute(Constants.WEB_CURRENT_MENU, "menu_fans");
                }
                if(url.startsWith("funds") || url.startsWith("refund") || url.startsWith("order")){
                    request.getSession().setAttribute(Constants.WEB_CURRENT_MENU, "menu_funds");
                }
                if(url.startsWith("mpp")){
                    request.getSession().setAttribute(Constants.WEB_CURRENT_MENU, "menu_mpp");
                }
            }
            filterChain.doFilter(request, response);
        }
    }


    @Bean
    public Filter protalFilter(){
        return new PortalFilter();
    }
    private static class PortalFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            if(request.getServletPath().startsWith("/portal/")){
                String userAgent = request.getHeader("User-Agent");
                LogUtil.getLogger().debug("当前portal请求的User-Agent=[{}]",userAgent);
                if(!userAgent.contains("MicroMessenger") || (!userAgent.contains("iPhone") && !userAgent.contains("Android") && !userAgent.contains("WindowsWechat"))){
                    response.setCharacterEncoding(HttpUtil.DEFAULT_CHARSET);
                    response.setContentType("text/plain; charset=" + HttpUtil.DEFAULT_CHARSET);
                    response.setHeader("Cache-Control", "no-cache");
                    response.setHeader("Pragma", "no-cache");
                    response.setDateHeader("Expires", 0);
                    PrintWriter out = response.getWriter();
                    out.print("请于iPhone或Android微信端访问");
                    out.flush();
                    out.close();
                    return;
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}