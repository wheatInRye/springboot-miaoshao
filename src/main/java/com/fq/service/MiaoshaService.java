package com.fq.service;

import com.fq.entify.MiaoshaOrder;
import com.fq.entify.MiaoshaUser;
import com.fq.entify.OrderInfo;
import com.fq.redis.RedisService;
import com.fq.redis.prefix.MiaoshaKey;
import com.fq.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/10 16:58
 * @Description:
 */
@Service
public class MiaoshaService {
    @Autowired
    private MiaoshaOrderService miaoshaOrderService;
    @Autowired
    private MiaoshaGoodsService miaoshaGoodsService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    RedisService redisService;

   // @Transactional
    public OrderInfo miaosha_order(MiaoshaUser miaoshaUser, GoodsVo goodsVo) {
        //减库存
        boolean success = miaoshaGoodsService.reduceStock(goodsVo);
        if (success){
            //下订单
            OrderInfo miaoshaOrderInfo = orderInfoService.createMiaoshaOrderInfo(miaoshaUser, goodsVo);
            miaoshaOrderService.creatMiaoshaOrder(miaoshaUser.getId(), goodsVo.getId(), miaoshaOrderInfo.getId());
            return miaoshaOrderInfo;
        }else {
            setGoodsOver(goodsVo.getId());
            return null;
        }
    }



    public long getMiaoshaResult(long userId, long goodsId) {
        //判断该用户是否秒杀成功过
        MiaoshaOrder order = miaoshaOrderService.getOrder(userId, goodsId);
        if (order != null){
            return order.getOrderId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver){
                return -1;
            }else {
                return 0;
            }
        }
    }

    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //构建内存图像
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // 背景颜色
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);

        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
       //生成随机数
        Random rdm = new Random();

        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // 生成随机验证码
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    public boolean checkVerifyCode(MiaoshaUser user, long goodsId, Integer verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId);
        return true;
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    private static char[] ops = new char[] {'+', '-', '*'};
    /*
     *       + - *
     */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }


    private void setGoodsOver(long id) {
        redisService.set(MiaoshaKey.isGoodsOver, ""+id, true);
    }
    private boolean getGoodsOver(long id) {
        return redisService.exists(MiaoshaKey.isGoodsOver, ""+id);
    }
}
