package com.fq.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fq.entify.MiaoshaOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/6 13:18
 * @Description:
 */
public interface MiaoshaOrderMapper extends BaseMapper<MiaoshaOrder> {
    @Select("select * from miaosha_order where userId = #{userId} and goodsId = #{goodsId}")
    public MiaoshaOrder getOrder(@Param("userId")long userId, @Param("goodsId")long goodsId);
}
