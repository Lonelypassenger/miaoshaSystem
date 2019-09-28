package com.kejia.miaosha.controller;

import com.kejia.miaosha.domin.MiaoshaOrder;
import com.kejia.miaosha.domin.MiaoshaUser;
import com.kejia.miaosha.domin.OrderInfo;
import com.kejia.miaosha.rabbitmq.MQSender;
import com.kejia.miaosha.rabbitmq.MiaoshaMessage;
import com.kejia.miaosha.redis.GoodsKey;
import com.kejia.miaosha.redis.MiaoshaKey;
import com.kejia.miaosha.redis.OrderKey;
import com.kejia.miaosha.redis.RedisService;
import com.kejia.miaosha.result.CodeMsg;
import com.kejia.miaosha.result.Result;
import com.kejia.miaosha.service.GoodsService;
import com.kejia.miaosha.service.MiaoshaService;
import com.kejia.miaosha.service.MiaoshaUserService;
import com.kejia.miaosha.service.OrderService;
import com.kejia.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 16:08 2019/9/25
 * @MODIFY:
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
    @Autowired
    MiaoshaUserService userService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;
    @Autowired
    MQSender mqSender;
    /**
     * 用来保存当前商品id的状态
     */
    private Map<Long,Boolean> localOverMap = new HashMap<>();

    /**
     *DATE:10:27 2019/9/28
     *DESCRIPTION:用于系统初始化。只要发现实现了上面的那个接口就会调用这个函数进行初始化
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList==null){
            return;
        }
        for(GoodsVo goods : goodsList){
            redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }


    @RequestMapping(value="/do_miaosha", method= RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user,
                                     @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        /**
         * 判断当前商品的秒杀是否是、已经结束了
         * 结束了就直接返回了
         * 没有结束再去访问我们的数据库
         */
        boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock,""+goodsId);
        if(stock<0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //如果没有什么问题的话：入队
        //发送用户信息以及哪个用户秒杀哪个商品
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        mqSender.sendMiaoshaMessage(mm);
        return Result.success(0);
/*        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);//10个商品，req1 req2
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        return Result.success(orderInfo);*/
    }

    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * */
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result  =miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for(GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsList);
        return Result.success(true);
    }


}
