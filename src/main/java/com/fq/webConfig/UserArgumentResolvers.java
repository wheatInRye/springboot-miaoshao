package com.fq.webConfig;

import com.fq.access.UserContext;
import com.fq.entify.MiaoshaUser;
import com.fq.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/8 19:00
 * @Description:
 */
@Service
public class UserArgumentResolvers implements HandlerMethodArgumentResolver {

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);

//        String paramToken = request.getParameter(MiaoshaUserService.COOKIE_TOKEN);
//        Cookie[] cookies = request.getCookies();
//        String cookieToken = null;
//        if (cookies != null){
//            for (Cookie cookie: cookies){
//                if (cookie.getName().equals(MiaoshaUserService.COOKIE_TOKEN)){
//                    cookieToken = cookie.getValue();
//                }
//            }
//        }
//
//        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
//            return "login";
//        }
//        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
//        MiaoshaUser miaoshaUser = miaoshaUserService.getByToken(response, token);
        return UserContext.getUser();
    }
}
