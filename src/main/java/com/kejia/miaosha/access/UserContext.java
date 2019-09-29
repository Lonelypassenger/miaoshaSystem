package com.kejia.miaosha.access;


import com.kejia.miaosha.domin.MiaoshaUser;

public class UserContext {
	/**
	 * ThreadLocal是多线程时保护线程安全的一种方式
	 * 每次放数据都是放到了当前线程中来，所以不存在多个线程之间的数据混杂，
	 * 导致的数据不安全
	 */
	private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();
	
	public static void setUser(MiaoshaUser user) {
		userHolder.set(user);
	}
	
	public static MiaoshaUser getUser() {
		return userHolder.get();
	}

}
