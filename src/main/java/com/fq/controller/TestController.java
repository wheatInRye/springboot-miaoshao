package com.fq.controller;

import com.fq.dao.UserMapper;
import com.fq.entify.User;
import com.fq.redis.RedisService;
import com.fq.redis.prefix.UserKey;
import com.fq.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/5 22:32
 * @Description:   测试
 */
@Controller
public class TestController {

    @Autowired
     UserMapper userMapper;
    @Autowired
    private RedisService redisService;
    @Autowired

    @RequestMapping("/rbs")
    @ResponseBody
    public Result<String> test2(){
        //rabbitMQSender.send("hello");
        return Result.success("Hell world");
    }


    //测试环境是否搭建成功
    @RequestMapping("/test")
    public String test1(){
        return "login";
    }

    //测试页面模板连接
    @RequestMapping("/testThymeleaf")
    public String testThymeleaf(Model model){
        model.addAttribute("test","测试");
        return "Hello";
    }

    //测试数据库连接
    @RequestMapping("/insert")
    @ResponseBody
    public User testInsert(Model model){
        model.addAttribute("test","测试");
        User user = new User();
        user.setUsername("冯庆1jjklkk");
        userMapper.insert(user);

        return user;
    }

    @RequestMapping("/testTransaction")
    @Transactional
    public void testTransation(){
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("王五");
        //userMapper.insert(user1);

        User user2 = new User();
        user1.setId(8);
        user1.setUsername("王");
        //userMapper.insert(user2);
   }

   //测试redis连接
   @RequestMapping("/redis/get")
   @ResponseBody
   public Result<String> get(){
       String v8 = redisService.get(UserKey.getById, "k8", String.class);
       return Result.success(v8);
   }
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<String> set(){
        boolean k8 = redisService.set(UserKey.getById, "k8", "123456789");
        return Result.success(k8+"");
    }

    @RequestMapping("/redis/exist")
    @ResponseBody
    public Result<String> exist(){
        boolean k8 = redisService.exists(UserKey.getById, "k8");
        return Result.success(k8+"");
    }
}
