package com.fq.util;

import java.util.UUID;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/8 12:30
 * @Description:
 */
public class UUIDUtil {

    public static String uuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }

}
