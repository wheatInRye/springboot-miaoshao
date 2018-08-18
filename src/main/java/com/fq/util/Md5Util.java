package com.fq.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/7 13:23
 * @Description:
 *      进行md5加密
 */
public class Md5Util {
    //盐值
    private static final String salt = "1a2b3c4d456g";

    //一次加密
    public static String getMd5(String str){
        return DigestUtils.md5Hex(str);
    }

    //盐值加密
    public static String inputassToForm(String inputpass){
        String str = salt.charAt(0)+salt.charAt(1)+inputpass+salt.charAt(6)+salt.charAt(7);
        return getMd5(str);
    }

    //盐值加密并保存到数据库
    public static String formPassToDB(String formPsss, String salt){
        String str = salt.charAt(0)+salt.charAt(1)+formPsss+salt.charAt(6)+salt.charAt(7);
        return getMd5(str);
    }
}
