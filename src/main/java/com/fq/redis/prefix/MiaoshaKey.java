package com.fq.redis.prefix;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/11 19:18
 * @Description:
 */
public class MiaoshaKey extends BasePrefix {

    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0, "isGoodsOver");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(0, "msp");
    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300, "msvc");

    public static MiaoshaKey accessWithexpireSeconds(int expireSeconds){
        return new MiaoshaKey(expireSeconds, "access");
    }
    public MiaoshaKey(String prefix) {
        super(prefix);
    }

    public MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
