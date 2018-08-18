package com.fq.redis.prefix;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/7 09:55
 * @Description:
 */
public class UserKey extends BasePrefix{

    public static  UserKey getById = new UserKey("id");
    public static  UserKey getByName = new UserKey("name");

    private UserKey(String prefix) {
        super(prefix);
    }


}
