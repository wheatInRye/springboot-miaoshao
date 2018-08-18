package com.fq.controller;

import com.fq.result.CodeMsg;
import com.fq.result.Result;
import com.fq.service.MiaoshaUserService;
import com.fq.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/7 14:50
 * @Description:
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private MiaoshaUserService miaoshaUserService;

    @RequestMapping(value = "/doLogin", method = RequestMethod.POST)
    @ResponseBody
    public Result doLogin(@Valid  LoginVo loginVo, HttpServletResponse response){

        //登陆
        miaoshaUserService.login(response, loginVo);
        return Result.success(CodeMsg.SUCCESS);
    }

}
