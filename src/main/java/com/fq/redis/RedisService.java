package com.fq.redis;

import com.alibaba.fastjson.JSON;
import com.fq.redis.prefix.KeyPrefix;
import com.fq.redis.prefix.MiaoshaKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/6 23:13
 * @Description:
 *        1.给容器注入JedisPppl
 *        2.对redis缓存进行操作
 */
@Service
public class RedisService {
    @Autowired
    private JedisPool jedisPool;

    /*
     *获取值key值
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz){
        Jedis jedis = null;
        try {
                jedis = jedisPool.getResource();

                //传入带有前缀的Key
                String realKey = prefix.getKey()+key;
                String str = jedis.get(realKey);
                T t = stringToBean(str, clazz);
            return t;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    /*
     *增加key
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if (str == null){
                return false;
            }
            //传入带有前缀的Key
            String realKey = prefix.getKey()+key;
            int seconds = prefix.expireSeconds();
            if (seconds <= 0){
                jedis.set(realKey, str);
            }else {
                jedis.setex(realKey, seconds, str);
            }
            return true;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }


    /*
     *p判断key是否存在
     */
    public <T> boolean exists(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //传入带有前缀的Key
            String realKey = prefix.getKey()+key;
            jedis.exists(realKey);
            return jedis.exists(realKey);
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    /*
     *增加值
     */
    public <T> Long incr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //传入带有前缀的Key
            String realKey = prefix.getKey()+key;

            return jedis.incr(realKey);
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    /*
     *减少值
     */
    public <T> Long decr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //传入带有前缀的Key
            String realKey = prefix.getKey()+key;

            return jedis.decr(realKey);
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public static  <T> String beanToString(T value) {
        if (value == null){
                return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;
        }
        if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        }
        if (clazz == String.class){
            return (String)value;
        }
        return JSON.toJSONString(value);
     }


    public static  <T> T stringToBean(String str,  Class<T> clazz) {
        if (str == null){
            return null;
        }

        if (clazz == int.class || clazz == Integer.class) {
            return (T)Integer.valueOf(str);
        }
        if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        }
        if (clazz == String.class){
            return (T)str;
        }

        return JSON.toJavaObject(JSON.parseObject(str), clazz);
    }

    public void delete(MiaoshaKey prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //传入带有前缀的Key
            String realKey = prefix.getKey()+key;

            jedis.del(realKey);
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }
}
