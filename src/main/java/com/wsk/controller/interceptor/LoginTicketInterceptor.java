package com.wsk.controller.interceptor;

import com.wsk.pojo.LoginTicket;
import com.wsk.pojo.UserInformation;
import com.wsk.service.UserInformationService;
import com.wsk.service.UserService;
import com.wsk.util.CookieUtil;
import com.wsk.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author hanW
 * @create 2022-08-04 11:54
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserInformationService  userInformationService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler)throws Exception{
        //从cookie中获取凭证
        String ticket = CookieUtil.getValue(request,"ticket");
        if(ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //根据凭证查询用户
                UserInformation user = userInformationService.selectByPrimaryKey((loginTicket.getUserId()));
                //在本次请求持有用户
                hostHolder.setUser(user);
//                //构建用户认证结果，并存入SecurityContext，以便于Security授权
//                Authentication authentication = new UsernamePasswordAuthenticationToken(
//                        user,user.getPassword(),userService.getAuthorities(user.getId()));
//                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,ModelAndView modelAndView) throws Exception {
        if(hostHolder != null){
            UserInformation user = hostHolder.getUser();
            if(user != null){
                modelAndView.addObject("loginUser",user);
            }
        }
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,Exception ex) throws Exception {
        hostHolder.clear();
//        SecurityContextHolder.clearContext();
    }
}
