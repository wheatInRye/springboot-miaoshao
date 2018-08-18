package com.fq.redis.prefix;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/11 19:18
 * @Description:
 */
public class GoodsKey extends BasePrefix {

    public static GoodsKey goodsList = new GoodsKey(60, "goods");
    public static GoodsKey miaoshaGoodsStock = new GoodsKey(0, "miaoshaGoodsStock");

    public GoodsKey(String prefix) {
        super(prefix);
    }

    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
