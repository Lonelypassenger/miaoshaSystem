package com.kejia.miaosha.service;

import com.kejia.miaosha.domin.MiaoshaOrder;
import com.kejia.miaosha.domin.MiaoshaUser;
import com.kejia.miaosha.domin.OrderInfo;
import com.kejia.miaosha.redis.MiaoshaKey;
import com.kejia.miaosha.redis.RedisService;
import com.kejia.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class MiaoshaService {
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;

	@Autowired
	RedisService redisService;
	@Transactional
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		//减库存 下订单 写入秒杀订单  因为有可能会减库存失败。所以这里加一个返回值用于待会儿进行判断
		boolean success = goodsService.reduceStock(goods);
		//order_info maiosha_order
		if(success) {
			return orderService.createOrder(user, goods);
		}else
		{
			setGoodsOver(goods.getId());
			return null;
		}
	}



	/**
	 *DATE:11:02 2019/9/28
	 *DESCRIPTION:该函数用来查询用户是否秒杀到了某东西。
	 */
	public long getMiaoshaResult(Long userId, long goodsId) {
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		if(order != null) {//秒杀成功
			return order.getOrderId();
		}else {
			//判断商品是不是买完了
			boolean isOver = getGoodsOver(goodsId);
			if(isOver) {
				return -1;
			}else {//如果不是因为卖完了，那么就返回0
				return 0;
			}
		}
	}
	private void setGoodsOver(Long goodsId) {
		redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
	}

	private boolean getGoodsOver(long goodsId) {
		return redisService.exists(MiaoshaKey.isGoodsOver, ""+goodsId);
	}

	public void reset(List<GoodsVo> goodsList) {
		goodsService.resetStock(goodsList);
		orderService.deleteOrders();
	}
	
}
