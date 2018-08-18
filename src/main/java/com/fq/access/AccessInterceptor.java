package com.fq.access;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.fq.entify.MiaoshaUser;
import com.fq.redis.RedisService;
import com.fq.redis.prefix.MiaoshaKey;
import com.fq.result.CodeMsg;
import com.fq.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/18 13:25
 * @Description:  1, 从redis缓存中获取相应cookie对应的MiaoshaUser
 *                 2, 利用拦截器注册@AccessLimit功能
 */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private RedisService redisService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            //获取User并保存到UserContext
            MiaoshaUser miaoshaUser = getMiaoshaUser(response, request);
            UserContext.setUser(miaoshaUser);


            HandlerMethod methodHandler = (HandlerMethod) handler;
            AccessLimit methodAnnotation = methodHandler.getMethodAnnotation(AccessLimit.class);
            if (methodAnnotation == null){
                return true;
            }
            int seconds = methodAnnotation.seconds();
            int count = methodAnnotation.maxCount();
            boolean needLogin = methodAnnotation.needLogin();
            String key = request.getRequestURI();
            if (needLogin){
                if (miaoshaUser == null){
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                key+= "_"+miaoshaUser.getId();
                Integer count1 = redisService.get(MiaoshaKey.accessWithexpireSeconds(seconds), key, Integer.class);
                if (count1 == null ){
                    redisService.set(MiaoshaKey.accessWithexpireSeconds(seconds), key, 1);
                }else if (count1 < count){
                    redisService.incr(MiaoshaKey.accessWithexpireSeconds(seconds), key);
                }else {
                    render(response,CodeMsg.ACCESS_LIMIT_REACHED);
                    return false;
                }

            }
        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg sessionError) {
        try {
            response.setContentType("application/json;charset=utf-8");
            ServletOutputStream outputStream = response.getOutputStream();
            String s = JSON.toJSONString(sessionError);
            outputStream.write(s.getBytes("utf-8"));
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MiaoshaUser getMiaoshaUser(HttpServletResponse response, HttpServletRequest request){
        String paramToken = request.getParameter(MiaoshaUserService.COOKIE_TOKEN);
        Cookie[] cookies = request.getCookies();
        String cookieToken = null;
        if (cookies != null){
            for (Cookie cookie: cookies){
                if (cookie.getName().equals(MiaoshaUserService.COOKIE_TOKEN)){
                    cookieToken = cookie.getValue();
                }
            }
        }

        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        MiaoshaUser miaoshaUser = miaoshaUserService.getByToken(response, token);
        return miaoshaUser;
    }
}
