package com.kejia.miaosha.rabbitmq;


import com.kejia.miaosha.domin.MiaoshaOrder;
import com.kejia.miaosha.domin.MiaoshaUser;
import com.kejia.miaosha.redis.RedisService;
import com.kejia.miaosha.service.GoodsService;
import com.kejia.miaosha.service.MiaoshaService;
import com.kejia.miaosha.service.OrderService;
import com.kejia.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 20:38 2019/9/27
 * @MODIFY:
 */
@Service
public class MQReceiver {
    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
    public void receive(String message) {
        log.info("receive message:"+message);
        MiaoshaMessage mm  = RedisService.stringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        miaoshaService.miaosha(user, goods);
    }



//    /**
//    *
//    *METHOD_NAME:
//    *PARAM:
//    *RETURN:
//    *DATE:21:15 2019/9/27
//    *DESCRIPTION:direct 模式,还有交换机模式
//     * 交换机模式，发送方不是直接将数据发送到对列当中了。而是将数据发送到交换机当中做一个路由。
//    */
//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message){
//        logger.info("receive message:"+message);
//    }
//    /**
//     *DATE:9:35 2019/9/28
//     *DESCRIPTION: 这个注解用来监听指定的对列，如果是出现了数据就执行这个函数来对其进行处理。
//     */
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message){
//        logger.info("topic queue1 receive message:"+message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message){
//        logger.info("topic queue2 receive message:"+message);
//    }
//
//    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
//    public void receiveHeader(String message){
//        logger.info("header queue message:"+new String(message));
//    }
}
