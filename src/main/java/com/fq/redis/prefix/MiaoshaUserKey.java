package com.fq.redis.prefix;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/8 13:05
 * @Description:
 */
public class MiaoshaUserKey extends BasePrefix{

    public final static int COOKIE_TOKEN_TIMEOUT = 3600*24*10;

    public static  MiaoshaUserKey token = new MiaoshaUserKey(COOKIE_TOKEN_TIMEOUT,"tk" );
    public static  MiaoshaUserKey getById = new MiaoshaUserKey(COOKIE_TOKEN_TIMEOUT,"miaoshaUser" );


    private MiaoshaUserKey(int timeOut, String prefix) {
        super(timeOut, prefix);
    }
}
