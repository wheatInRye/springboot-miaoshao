package com.fq.rabbitmq;

import com.fq.entify.MiaoshaOrder;
import com.fq.entify.MiaoshaUser;
import com.fq.entify.OrderInfo;
import com.fq.redis.RedisService;
import com.fq.service.MiaoshaGoodsService;
import com.fq.service.MiaoshaOrderService;
import com.fq.service.MiaoshaService;
import com.fq.vo.GoodsVo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MQReceiver {

		@Autowired
        private MiaoshaGoodsService miaoshaGoodsService;
		@Autowired
        private MiaoshaOrderService miaoshaOrderService;
		@Autowired
        private MiaoshaService miaoshaService;

		@RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
		public void receive(String message) {
			MiaoshaMessage miaoshaBean = RedisService.stringToBean(message, MiaoshaMessage.class);
            MiaoshaUser user = miaoshaBean.getUser();
            long goodsId = miaoshaBean.getGoodsId();

            //判断库存状态
            GoodsVo goodsVo = miaoshaGoodsService.getGoodsVoByGoodsId(goodsId);
            Integer stock = goodsVo.getStockCount();
            if (stock <= 0 && stock != -1){
                return;
            }

            //判断该用户是否秒杀成功过
            MiaoshaOrder order = miaoshaOrderService.getOrder(user.getId(), goodsId);
            if (order != null){
                return;
            }
            //进入秒杀
            OrderInfo orderInfo = miaoshaService.miaosha_order(user, goodsVo);
        }
//		
//		@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
//		public void receiveTopic1(String message) {
//			log.info(" topic  queue1 message:"+message);
//		}
//		
//		@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
//		public void receiveTopic2(String message) {
//			log.info(" topic  queue2 message:"+message);
//		}
//		
//		@RabbitListener(queues=MQConfig.HEADER_QUEUE)
//		public void receiveHeaderQueue(byte[] message) {
//			log.info(" header  queue message:"+new String(message));
//		}
//		
		
}
