package com.fq.service;

import com.fq.dao.MiaoshaGoodsMapper;
import com.fq.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/10 17:55
 * @Description:
 */
@Service
public class MiaoshaGoodsService {

    @Autowired
    private MiaoshaGoodsMapper miaoshaGoodsMapper;

    public List<GoodsVo> getGoodsVo(){
        List<GoodsVo> goodsVo = miaoshaGoodsMapper.getGoodsVo();
        return goodsVo;
    }

    public GoodsVo getGoodsVoByGoodsId(Long goodsId) {
        GoodsVo goodsVo = miaoshaGoodsMapper.getGoodsVoByGoodsId(goodsId);
        return goodsVo;
    }


    public boolean reduceStock(GoodsVo goodsVo) {
        int stock_before = miaoshaGoodsMapper.selectStockById(goodsVo.getId());
        miaoshaGoodsMapper.updateStockById(goodsVo.getId());
        int stock_after = miaoshaGoodsMapper.selectStockById(goodsVo.getId());
        if (stock_before == stock_after){
            return false;
        }
        return true;
    }

    public int selectStockById(long goodsId){
        int i = miaoshaGoodsMapper.selectStockById(goodsId);
        return i;
    }
}
