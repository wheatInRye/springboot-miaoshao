package com.fq.redis.prefix;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/7 09:32
 * @Description:
 */
public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;
    private String prefix;

    public BasePrefix(String prefix){
       this(0, prefix);
    }

    public BasePrefix(int expireSeconds, String prefix){
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public int expireSeconds(){//默认0代表永不过期
        return expireSeconds;
    }

    public String getKey(){
        String prefixName = getClass().getSimpleName();
        return prefixName+ ":"+prefix+"-";
    }
}
