package com.fq.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.fq.dao.MiaoshaOrderMapper;
import com.fq.entify.MiaoshaOrder;
import com.fq.entify.MiaoshaUser;
import com.fq.vo.GoodsVo;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/10 14:12
 * @Description:
 */
@Service
public class MiaoshaOrderService {

    @Autowired
    private MiaoshaOrderMapper miaoshaOrderMapper;

    public MiaoshaOrder getOrder(long userId, long goodsId){
        MiaoshaOrder order = miaoshaOrderMapper.getOrder(userId, goodsId);

        return order;
    }

    public void creatMiaoshaOrder(long miaoshaUserId, long goodsId, long orderInfoId) {
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setUserId(miaoshaUserId);
        miaoshaOrder.setGoodsId(goodsId);
        miaoshaOrder.setOrderId(orderInfoId);
        miaoshaOrderMapper.insert(miaoshaOrder);
    }

    public MiaoshaOrder getOrderById(long orderId){
        MiaoshaOrder miaoshaOrder = miaoshaOrderMapper.selectById(orderId);
        return miaoshaOrder;
    }
}
