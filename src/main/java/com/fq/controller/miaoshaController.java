package com.fq.controller;

import com.fq.access.AccessLimit;
import com.fq.entify.MiaoshaOrder;
import com.fq.entify.MiaoshaUser;
import com.fq.rabbitmq.MQSender;
import com.fq.rabbitmq.MiaoshaMessage;
import com.fq.redis.RedisService;
import com.fq.redis.prefix.GoodsKey;
import com.fq.redis.prefix.MiaoshaKey;
import com.fq.result.CodeMsg;
import com.fq.result.Result;
import com.fq.service.MiaoshaGoodsService;
import com.fq.service.MiaoshaOrderService;
import com.fq.service.MiaoshaService;
import com.fq.util.UUIDUtil;
import com.fq.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/10 13:54
 * @Description:
 */
@Controller
@RequestMapping("/miaosha")
public class miaoshaController implements InitializingBean {

    @Autowired
    private MiaoshaOrderService miaoshaOrderService;
    @Autowired
    private MiaoshaGoodsService miaoshaGoodsService;
    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MQSender mqSender;

    //private static Logger log = LoggerFactory.getLogger(miaoshaController.class);
    private Map<Long, Boolean> stockOverMap = new HashMap<>();


    /*
     * 系统初始化
     */
    @Override
    public void afterPropertiesSet() {
        List<GoodsVo> goodsVo = miaoshaGoodsService.getGoodsVo();
        if (goodsVo == null){
            return;
        }
        for (GoodsVo goods: goodsVo){
            redisService.set(GoodsKey.miaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
            stockOverMap.put(goods.getId(), false);
        }
    }

    /*
      *  入队轮询
      *  成功:orderId
      *  失败:-1
       *排队: 0
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaosha1Result(@RequestParam("goodsId") long goodsId, MiaoshaUser user){
        //判断用户是否登陆
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);

        return Result.success(result);
    }

    /*
    *  生成验证码
     */
    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> miaosha1VerifyCode(@RequestParam("goodsId") long goodsId, MiaoshaUser user, HttpServletResponse response){
        //判断用户是否登陆
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        try {
            BufferedImage image  = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

    /*
      * 返回随机的秒杀地址
     */
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> miaosha1Path(@RequestParam("goodsId") long goodsId, @RequestParam("verifyCode") Integer verifyCode, MiaoshaUser user){
        //判断用户是否登陆
        System.out.println("path======="+verifyCode);
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //判断验证码是否正确
        if (verifyCode == null){
            return Result.error(CodeMsg.VERIFY_CODE_NULL);
        }
        boolean isCorrect = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if (!isCorrect){
            return Result.error(CodeMsg.VERIFY_CODE_ERROE);
        }

        String uuid = UUIDUtil.uuid();
        redisService.set(MiaoshaKey.getMiaoshaPath, ""+goodsId, uuid);
        return Result.success(uuid);
    }


    /*
    *  进行秒杀
     */
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/do_miaoshaStatic/{path}", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha1(@RequestParam("goodsId") long goodsId, MiaoshaUser user, @PathVariable("path") String path){
        //判断用户是否登陆
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //判断请求地址是否正确
        String realyPath = redisService.get(MiaoshaKey.getMiaoshaPath, "" + goodsId, String.class);
        if (!path.equals(realyPath)){
            return Result.error(CodeMsg.MIAOSHA_PATH);
        }

        //内存标记
        Boolean isOver = stockOverMap.get(goodsId);
        if(isOver){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //预减库存
        Long stock = redisService.decr(GoodsKey.miaoshaGoodsStock, "" + goodsId);
        if (stock <= 0){
            stockOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //判断该用户是否秒杀成功过
        MiaoshaOrder order = miaoshaOrderService.getOrder(user.getId(), goodsId);
        if (order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //入队
        MiaoshaMessage message = new MiaoshaMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        mqSender.sendMiaoshaMessage(message);

        return Result.success(0);
    }
}
