package com.fq.controller;

import com.fq.entify.MiaoshaUser;
import com.fq.entify.OrderInfo;
import com.fq.result.CodeMsg;
import com.fq.result.Result;
import com.fq.service.MiaoshaGoodsService;
import com.fq.service.MiaoshaOrderService;
import com.fq.service.OrderInfoService;
import com.fq.vo.GoodsVo;
import com.fq.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * @Auther: 冯庆
 * @Date: 2018/8/13  13:02
 * @Description:
 */
@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private OrderInfoService orderInfoService;
	@Autowired
	private MiaoshaOrderService miaoshaOrderService;
	@Autowired
    private MiaoshaGoodsService miaoshaGoodsService;
	
    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, MiaoshaUser user, @RequestParam("orderId") long orderId){
        System.out.println(orderId);
    	//判断用户是否登陆
		if (user == null){
			return Result.error(CodeMsg.SESSION_ERROR);
		}

        OrderInfo orderInfo = orderInfoService.getOrderInfoById(orderId);
        if (orderInfo == null){
			return Result.error(CodeMsg.ORDER_NOT_EXIST);
		}

        long goodsId = orderInfo.getGoodsId();
        GoodsVo goodsVo = miaoshaGoodsService.getGoodsVoByGoodsId(goodsId);

        //将goodsVo和orderInfo封装进orderDetailVo
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoods(goodsVo);
        orderDetailVo.setOrder(orderInfo);

        return Result.success(orderDetailVo);
    }
    
}
