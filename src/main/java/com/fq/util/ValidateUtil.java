package com.fq.util;


import com.alibaba.druid.util.StringUtils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/7 19:54
 * @Description:
 */
public class ValidateUtil {
    private static final Pattern mobile_pattern =  Pattern.compile("1\\d{10}");

    public static boolean isMobile(String mobile){
        if (StringUtils.isEmpty(mobile)){
            return false;
        }

        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();
    }

}
