package com.fq.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.fq.vo.GoodsVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/10 17:56
 * @Description:
 */
public interface MiaoshaGoodsMapper extends BaseMapper<GoodsVo> {
    @Select("SELECT g.*,mg.stockCount, mg.startDate, mg.endDate, mg.miaoshaPrice FROM miaosha_goods mg LEFT JOIN goods g ON mg.goodsId = g.id")
    public List<GoodsVo> getGoodsVo();

    @Select("SELECT g.*,mg.stockCount, mg.startDate, mg.endDate, mg.miaoshaPrice FROM miaosha_goods mg LEFT JOIN goods g ON mg.goodsId = g.id where g.id= #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    @Select("select stockCount from miaosha_goods where id = #{id}")
    public int selectStockById(@Param("id") long goodsId);

    @Select("UPDATE miaosha_goods SET stockCount = stockCount-1 WHERE id =#{id} and stockCount > 0")
    public void updateStockById( @Param("id")long id);
}
