package com.kejia.miaosha.config;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kejia.miaosha.domin.MiaoshaUser;
import com.kejia.miaosha.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

	@Autowired
	MiaoshaUserService userService;
	/**
	*
	*METHOD_NAME:
	*PARAM:MethodParameter parameter 是spring对被注解修饰过参数的包装，从其中能拿到参数的反射相关信息。
	*RETURN:
	*DATE:14:25 2019/9/25
	*DESCRIPTION:传入一个参数，用以判断此参数是否能够使用该解析器。
	 * 框架会将每一个MethodParameter传入supportsParameter测试是否能够被处理，如果能够，就使用resolveArgument处理。
	 * 很明显这个拦截器器只会对参数类型是MiaoshaUser类的请求进行拦截处理。
	*/
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> clazz = parameter.getParameterType();
		return clazz== MiaoshaUser.class;
	}
	/**
	*
	*METHOD_NAME:
	*PARAM:
	*RETURN:
	*DATE:14:25 2019/9/25
	*DESCRIPTION:解析函数，传入必要信息，计算并返回一个值
	*/
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		
		String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
		String cookieToken = getCookieValue(request, MiaoshaUserService.COOKI_NAME_TOKEN);
		if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return null;
		}
		String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
		return userService.getByToken(response, token);
	}

	private String getCookieValue(HttpServletRequest request, String cookiName) {

		Cookie[]  cookies = request.getCookies();
		if(cookies==null||cookies.length<=0){
			return null;
		}
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(cookiName)) {
				return cookie.getValue();
			}
		}
		return null;
	}

}
