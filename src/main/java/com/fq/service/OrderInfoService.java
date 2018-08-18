package com.fq.service;

import com.fq.dao.OrderInfoMapper;
import com.fq.entify.MiaoshaUser;
import com.fq.entify.OrderInfo;
import com.fq.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/10 17:19
 * @Description:
 */
@Service
public class OrderInfoService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    public OrderInfo createMiaoshaOrderInfo(MiaoshaUser miaoshaUser, GoodsVo goodsVo){
        //创建一个OrderInfo对象并加入数据库
        OrderInfo info = new OrderInfo();
        info.setGoodsId(goodsVo.getId());
        info.setCreateDate(new Date());
        info.setDeliveryAddrId(0L);
        info.setGoodsCount(1);
        info.setGoodsName(goodsVo.getGoodsName());
        info.setGoodsPrice(goodsVo.getGoodsPrice());
        info.setOrderChannel(1);
        info.setUserId(miaoshaUser.getId());
        info.setPayDate(new Date());
        info.setStatus(1);
        orderInfoMapper.insert(info);

        //查询该对象并返回
        OrderInfo orderInfo = orderInfoMapper.selectOne(info);
        return orderInfo;
    }

    public OrderInfo getOrderInfoById(long orderId){
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        return orderInfo;
    }
}
