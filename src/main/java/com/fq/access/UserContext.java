package com.fq.access;

import com.fq.entify.MiaoshaUser;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/18 13:46
 * @Description:   存放同线程的MiaoshaUser
 */
public class UserContext {

    private static ThreadLocal<MiaoshaUser> userThreadLocal = new ThreadLocal<>();

    public static void setUser(MiaoshaUser miaoshaUser){
        userThreadLocal.set(miaoshaUser);
    }
    public static MiaoshaUser getUser(){
        return userThreadLocal.get();
    }

}
