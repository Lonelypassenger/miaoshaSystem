package com.kejia.miaosha.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.kejia.miaosha.dao.MiaoshaUserDao;
import com.kejia.miaosha.domin.MiaoshaUser;
import com.kejia.miaosha.exception.GlobalException;
import com.kejia.miaosha.redis.MiaoshaUserKey;
import com.kejia.miaosha.redis.RedisService;
import com.kejia.miaosha.result.CodeMsg;
import com.kejia.miaosha.utils.MD5Util;
import com.kejia.miaosha.utils.UUIDUtil;
import com.kejia.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MiaoshaUserService {
	
	
	public static final String COOKI_NAME_TOKEN = "token";
	
	@Autowired
	MiaoshaUserDao miaoshaUserDao;
	
	@Autowired
	RedisService redisService;
	
	public MiaoshaUser getById(long id) {
		MiaoshaUser user = redisService.get(MiaoshaUserKey.getById,""+id,MiaoshaUser.class);
		if(user!=null){
			return user;
		}
		//取数据库
		user = miaoshaUserDao.getById(id);
		if(user!=null) {
			redisService.set(MiaoshaUserKey.getById, "" + id, user);
		}
		return user;
	}


	public Boolean updatePassword(String token, long id,String formPass) {
		//取user
		MiaoshaUser user = getById(id);
		if(user==null){
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		//更新数据库
		MiaoshaUser toBeUpdate = new MiaoshaUser();
		toBeUpdate.setId(id);
		toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass,user.getSalt()));
		miaoshaUserDao.update(toBeUpdate);
		//清理缓存  关于token和id
		//处理缓存 删除id
		redisService.delete(MiaoshaUserKey.getById, ""+id);
		//更新token token要用来登录验证的，不能直接删除掉
		user.setPassword(toBeUpdate.getPassword());
		redisService.set(MiaoshaUserKey.token, token, user);
		return true;
	}



	public MiaoshaUser getByToken(HttpServletResponse response, String token) {
		if(StringUtils.isEmpty(token)) {
			return null;
		}
		MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
		//延长有效期
		if(user != null) {
			addCookie(response, token, user);
		}
		return user;
	}
	

	public String login(HttpServletResponse response, LoginVo loginVo) {
		if(loginVo == null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		String formPass = loginVo.getPassword();
		//判断手机号是否存在
		MiaoshaUser user = getById(Long.parseLong(mobile));
		if(user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		//验证密码
		String dbPass = user.getPassword();
		String saltDB = user.getSalt();
		String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
		if(!calcPass.equals(dbPass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		//生成cookie
		String token	 = UUIDUtil.uuid();
		addCookie(response, token, user);
		return token;
	}
	
	private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
		redisService.set(MiaoshaUserKey.token, token, user);
		Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
		cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}
