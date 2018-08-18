package com.fq.service;

import com.alibaba.druid.util.StringUtils;
import com.fq.dao.MiaoshaUserMapper;
import com.fq.entify.MiaoshaUser;
import com.fq.entify.User;
import com.fq.exception.GlobalException;
import com.fq.redis.RedisService;
import com.fq.redis.prefix.MiaoshaUserKey;
import com.fq.redis.prefix.UserKey;
import com.fq.result.CodeMsg;
import com.fq.util.Md5Util;
import com.fq.util.UUIDUtil;
import com.fq.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/7 22:02
 * @Description:
 */
@Service
public class MiaoshaUserService {

    public final static String COOKIE_TOKEN = "token";

    @Autowired
    private MiaoshaUserMapper miaoshaUserMapper;
    @Autowired
    private RedisService redisService;

    public MiaoshaUser getById(long userId){
        //从缓存取
        MiaoshaUser miaoshaUser = redisService.get(UserKey.getById, "" + userId, MiaoshaUser.class);
        if (miaoshaUser != null){
            return miaoshaUser;
        }
        //数据库取
        miaoshaUser = miaoshaUserMapper.selectById(userId);
        if (miaoshaUser != null){
            redisService.set(UserKey.getById, ""+userId, miaoshaUser);
        }

        return miaoshaUser;
    }


    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        //获取数据库中用户手机号和加密密码
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if (miaoshaUser == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        //密码验证
        String dbPass = miaoshaUser.getPassword();
        String salt = miaoshaUser.getSalt();
        String userpass = Md5Util.formPassToDB(password, salt);

        if (!dbPass.equals(userpass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(response, miaoshaUser, token);
        return true;
    }

    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);

        //延长cookie有效期
        if (miaoshaUser != null){
            addCookie(response, miaoshaUser, token);
        }

        return miaoshaUser;
    }

    private void addCookie(HttpServletResponse response, MiaoshaUser miaoshaUser, String token){

        //生成cookie
        redisService.set(MiaoshaUserKey.token, token, miaoshaUser);
        Cookie cookie = new Cookie(COOKIE_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
