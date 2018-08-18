package com.fq.redis.prefix;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/7 09:29
 * @Description:   redis缓存Key前缀接口
 */
public interface KeyPrefix {

    public int expireSeconds();
    public String getKey();
}
