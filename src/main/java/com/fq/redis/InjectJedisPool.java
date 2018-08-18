package com.fq.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/6 23:25
 * @Description:
 *      给容器注入JedisPppl
 */

@Component
public class InjectJedisPool {
    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool jedisPoolFactory(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        jedisPoolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisConfig.getHost(),
                                            redisConfig.getPort(), redisConfig.getTimeout()*1000,
                                            redisConfig.getPassword(), 0);
        return jedisPool;
    }
}
