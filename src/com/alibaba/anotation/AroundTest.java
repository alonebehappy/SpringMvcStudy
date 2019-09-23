package com.alibaba.anotation;

import com.sxk.entity.Token;
import com.sxk.service.AuthorityService;
import com.sxk.service.TokenService;
import org.apache.commons.fileupload.RequestContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by stonegeek on 2017/3/4.
 */
@Aspect
public class AroundTest {
    @Autowired
    TokenService tokenService;
    @Autowired
    AuthorityService authorityService;
    @Pointcut("execution(public * com.sxk.controller.testcontroller.*(..))")
    public void testaround(){}

    @Around("testaround()")
    public Object test(ProceedingJoinPoint jp) throws Throwable{
        System.out.println("开始验证Token权限。。。。");
        HttpServletRequest request= ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String tokenName=this.getToken(request);
        String url=this.getURL(request);
        String method=this.getMethod(request);
        System.out.println("Token为："+tokenName);
        System.out.println("URL为："+url);

        System.out.println("Method为："+method);
        Token token=tokenService.findByTokenName(tokenName);
        if(token==null)
            return "{'result':'Token not exsits'}";
        Timestamp creatTime=token.getCreateTime();
        int len=token.getEffectiveTime();
        Timestamp timeNow=new Timestamp(new Date().getTime());
        List<String> allApi=null;
        if((creatTime.getTime()+len*1000*60)>=timeNow.getTime()){
            allApi=authorityService.getAPI(tokenName);
            System.out.println(allApi);
            if(allApi!=null&&allApi.contains(url)){
                System.out.println("Token验证通过！！！");
                return jp.proceed();
            }else {
                System.out.println("验证失败！！！");
                return "{'result':'No authority for this API!!'}";
            }
        }else {
            System.out.println("The Token is Timeout");
            return "{'result':'The Token is Timeout!!'}";
        }
    }
    public String getToken(HttpServletRequest request){
        return request.getHeader("token");
    }
    public String getURL(HttpServletRequest request){
        return request.getRequestURI();
    }
    public String getMethod(HttpServletRequest request){
        return request.getMethod();
    }

