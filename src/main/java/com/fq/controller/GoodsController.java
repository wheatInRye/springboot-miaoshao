package com.fq.controller;

import com.alibaba.druid.util.StringUtils;
import com.fq.entify.MiaoshaUser;
import com.fq.redis.RedisService;
import com.fq.redis.prefix.GoodsKey;
import com.fq.result.Result;
import com.fq.service.MiaoshaGoodsService;
import com.fq.service.MiaoshaUserService;
import com.fq.vo.GoodsDetailVo;
import com.fq.vo.GoodsVo;
import com.fq.webConfig.SpringWebContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/7 14:50
 * @Description:
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private MiaoshaGoodsService miaoshaGoodsService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    private ApplicationContext applicationContext;

    /*
      *  商品列表
     */
    @RequestMapping(value = "/goods_list")
    public String to_list(Model model, MiaoshaUser miaoshaUser) {
        model.addAttribute("miaoshaUser", miaoshaUser);
        return "goods_list";
    }

    /*
     *优化前压力测试结果:Thread Group 100000 1 1 -------QPS 1000
     *    后                                                1700
     *
     */
    @RequestMapping(value = "/getGoodsVo", produces = "text/html")
    @ResponseBody
    public String getGoodsVo(Model model, HttpServletResponse response, HttpServletRequest request, MiaoshaUser user) {
        if (user == null) {
            return "login";
        }
        //取页面缓存
        String html = redisService.get(GoodsKey.goodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
           // return html;
        }
        //渲染-----springwebcontext被取消
        List<GoodsVo> goodsVo = miaoshaGoodsService.getGoodsVo();
        model.addAttribute("goodsList", goodsVo);
        SpringWebContextUtil webContext = new SpringWebContextUtil(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", webContext);
        //保存到缓存
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.goodsList, "", html);
        }
        return html;
    }

    /*
     *  页面缓存
     */
    @RequestMapping(value = "/to_detail/{goodsId}")
    @ResponseBody
    public String to_detail(Model model, MiaoshaUser user, HttpServletRequest request, HttpServletResponse response,
                            @PathVariable("goodsId") long goodsId) {
        model.addAttribute("user", user);

        //url缓存
        String html = redisService.get(GoodsKey.goodsList, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        GoodsVo goods = miaoshaGoodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long nowAt = System.currentTimeMillis();

        //秒杀状态
        int miaoshaStatus = 0;
        int remainSeconds = 0;

        if (startAt > nowAt) {//秒杀还没开始
            miaoshaStatus = 0;
            remainSeconds = (int) (startAt - nowAt) / 1000;
        } else if (endAt < nowAt) {//结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else { //进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        //渲染-----springwebcontext被取消
        SpringWebContextUtil webContext = new SpringWebContextUtil(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("to_goodsDetail", webContext);
        //保存到缓存
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.goodsList, "" + goodsId, html);
        }
        return html;
    }


    @RequestMapping(value="/detail")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model,MiaoshaUser user,
                                        @RequestParam("goodsId")long goodsId) {

        GoodsVo goods = miaoshaGoodsService.getGoodsVoByGoodsId(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        int remainSeconds = 0;

        if(now < startAt ) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setMiaoshaStatus(miaoshaStatus);
        return Result.success(vo);
    }

}
