package com.github.wangcaizhcn.config.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthService {

	// 默认账号就是admin，这么个小玩意，不用整那么多账号
	private static final String username = "admin";
	
	private static final String ISSUER = "legend";
	
	// 访问token有效时间30分钟
	private static final Long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 30;
	
	// 刷新token有效时间2小时
	private static final Long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 2;
	
	// 加密的秘钥
	private static final String AUTH_SECRET = "1234567890!@#$%^&*()abcdefgHIJKLMNopqRSTuvwXYZ{}[]<>";
	
	public static void saveUser(String password, String type) {
		User user = new User();
		user.setPassword(password);
		user.setType(type);
		user.setUsername(username);
        
		String path = System.getProperty("user.dir");
		File file = new File(path, "user.ser");
		try(FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);) {
			oos.writeObject(user);
		} catch(Exception e) {
			System.out.println("保存用户错误");
			throw new RuntimeException(e);
		}
	}
	
	public static User getUser() {
		String path = System.getProperty("user.dir");
		File file = new File(path, "user.ser");
		try(FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);) {
			return (User)ois.readObject();
		} catch(Exception e) {
			System.out.println("获取用户信息失败");
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获取一组token
	 * @param username
	 * @return
	 */
	public static Map<String, String> getToken(String username) {
		
		Map<String, Object> claims = new HashMap<>(16);
		claims.put("username", username);
		
		String accessToken =  generateToken(username, claims, ACCESS_TOKEN_EXPIRATION);
		String refreshToken =  generateToken(username, claims, REFRESH_TOKEN_EXPIRATION);
		
		Map<String, String> token = new HashMap<>();
		token.put("accessToken", accessToken);
		token.put("refreshToken", refreshToken);
		
		return token;
	}
	
	// 生成一个token
	private static String generateToken(String subject, Map<String, Object> claims, long expiration) {
		
		String id = UUID.randomUUID().toString();
		
		return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setId(id)
            .setIssuer(ISSUER)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .compressWith(CompressionCodecs.DEFLATE)
            .signWith(SignatureAlgorithm.HS256, AUTH_SECRET)
            .compact();
    }
	
	/**
	 * 校验Token
	 * @param token
	 * @return
	 */
	public static String checkToken(String token) {
        try {
        	Claims claims = Jwts.parser()
                .setSigningKey(AUTH_SECRET)
                .parseClaimsJws(token)
                .getBody();
            return claims.getSubject();
        } catch (Exception e) {
        	throw new RuntimeException("-1", e);
        }
    }
}
