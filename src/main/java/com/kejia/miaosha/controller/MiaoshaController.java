package com.kejia.miaosha.controller;

import com.kejia.miaosha.domin.MiaoshaOrder;
import com.kejia.miaosha.domin.MiaoshaUser;
import com.kejia.miaosha.domin.OrderInfo;
import com.kejia.miaosha.redis.RedisService;
import com.kejia.miaosha.result.CodeMsg;
import com.kejia.miaosha.service.GoodsService;
import com.kejia.miaosha.service.MiaoshaService;
import com.kejia.miaosha.service.MiaoshaUserService;
import com.kejia.miaosha.service.OrderService;
import com.kejia.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @AUTHOR :yuankejia
 * @DESCRIPTION:
 * @DATE:CRETED: IN 16:08 2019/9/25
 * @MODIFY:
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
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

    @RequestMapping("/do_miaosha")
    public String list(Model model, MiaoshaUser user,
                       @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user==null) return "login";
        //判断有没有库存
        GoodsVo vo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = vo.getStockCount();
        if(stock <=0){
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";

        }

        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order!=null){
            model.addAttribute("errmsg",CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }

        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user,vo);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",vo);
        return "order_detail";
    }
}
