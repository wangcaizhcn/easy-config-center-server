package com.github.wangcaizhcn.config.util;

import java.util.Random;

/**
 * 随机数的生成
 * @author 王财
 * @date 2021年4月29日
 */
public class KeyGenerateUtil {

	private static final char[] c = {'0', '1', '2', '3', '4', '5', 
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C',
			'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
			'Z', '-', '_'};
	
	/**
	 * 生成8位的随机数，高并发不保证唯一，适用于允许不唯一的随机数业务，如秘钥、盐值等
	 * @return
	 */
	public static String getRandomKey8Bit() {
		int index = 0;
		char[] keys = new char[8];
		long l = System.currentTimeMillis();
		while(l > 0 && index < 8) {
			keys[index ++] = c[(int)(l & 63)];
			l = l >> 6;
		}
		if(index >= 8) {
			return new String(keys);
		}
		Random r = new Random();
		while(index < 8) {
			keys[index ++] = c[r.nextInt(63)];
		}
		return new String(keys);
	}
	
}
