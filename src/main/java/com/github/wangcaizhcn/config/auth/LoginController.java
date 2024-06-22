package com.github.wangcaizhcn.config.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wangcaizhcn.config.basic.ApiResult;
import com.github.wangcaizhcn.config.basic.ApiResultTemplate;
import com.github.wangcaizhcn.config.basic.CommonResultCodeConstant;
import com.github.wangcaizhcn.config.util.DESUtil;
import com.github.wangcaizhcn.config.util.KeyGenerateUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/login")
public class LoginController {

	/**
	 * 获取临时加密的秘钥对
	 * @return
	 */
	@GetMapping("/key")
	public ApiResult<Map<String, String>> getRandomKey() {
		String key = UUID.randomUUID().toString();
		String encode_key = KeyGenerateUtil.getRandomKey8Bit();
		
		KeyStoreModel ks = KeyStoreModel.getInstance();
		ks.addKey(key, encode_key);
		
		Map<String, String> map = new HashMap<>();
		map.put("key", key);
		map.put("encode_key", encode_key);
		
		return ApiResultTemplate.success(map);
	}
	
	@PostMapping("/")
	public void login(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginBean bean) {
		// 校验
		Assert.hasText(bean.getUsername(), "用户名必填");
		Assert.hasText(bean.getPassword(), "密码必填");
		Assert.hasText(bean.getKey(), "登录key必填");
		
		// 密码解密
		KeyStoreModel ks = KeyStoreModel.getInstance();
		String encodeKey = ks.getEncodeKey(bean.getKey());
		Assert.hasText(encodeKey, "登录key已过期，请重新登录");
		String realPassword = DESUtil.decrypt(bean.getPassword(), encodeKey);
		
		// 校验密码
		User user = AuthService.getUser();
		if(user.getUsername().equals(bean.getUsername()) && user.getPassword().equals(realPassword)) {
			// 生成token
			Map<String, String> token = AuthService.getToken(user.getUsername());
			ObjectMapper mapper = new ObjectMapper();
			
			// token回写
			response.setHeader("Authorization", token.get("accessToken"));
			response.setHeader("Authorization-Refresh", token.get("refreshToken"));
			response.setHeader("Access-Control-Expose-Headers", "Authorization,Authorization-Refresh");
			
			response.setContentType("application/json;charset=UTF-8");
			ApiResult<User> result = new ApiResult<>();
			result.setCode(CommonResultCodeConstant.REQUEST_SUCCESS);
			result.setMessage("登录成功");
			result.setData(user);
		    try {
				response.getWriter().append(mapper.writeValueAsString(result));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException("用户名或密码错误");
		}
	}
	
	@PostMapping("/refresh")
	public void refresh(HttpServletRequest request, HttpServletResponse response, @RequestHeader(name = "Authorization-Refresh") String refreshToken) {
		Assert.hasText(refreshToken, "没有token");
		String username = AuthService.checkToken(refreshToken);
		// 生成token
		Map<String, String> token = AuthService.getToken(username);
		ObjectMapper mapper = new ObjectMapper();
		
		// token回写
		response.setHeader("Authorization", token.get("accessToken"));
		response.setHeader("Authorization-Refresh", token.get("refreshToken"));
		response.setHeader("Access-Control-Expose-Headers", "Authorization,Authorization-Refresh");
		
		response.setContentType("application/json;charset=UTF-8");
		ApiResult<Map<String, Object>> result = new ApiResult<>();
		result.setCode(CommonResultCodeConstant.REQUEST_SUCCESS);
		result.setMessage("登录成功");
	    try {
			response.getWriter().append(mapper.writeValueAsString(result));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
