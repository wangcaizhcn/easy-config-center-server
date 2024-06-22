package com.github.wangcaizhcn.config.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.springframework.util.DigestUtils;

/**
 * MD5摘要工具类
 * @author wangcai
 */
public class MD5Util {

	/**
	 * 对文本进行摘要
	 * @param text 需要进行摘要的文本内容
	 * @return
	 */
	public static String md5(String text) {
		String code = DigestUtils.md5DigestAsHex(text.getBytes());
	    return code.toUpperCase();
	}
	
	/**
	 * 对附件流进行摘要，该方法不关闭流，需要调用者进行手动关闭
	 * @param is
	 * @return
	 */
	public static String md5(InputStream is) {
		String code;
		try {
			code = DigestUtils.md5DigestAsHex(is);
			return code.toUpperCase();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 对文件进行摘要。
	 * @param file
	 * @return
	 */
	public static String md5(File file) {
		try(InputStream is = new FileInputStream(file);) {
			return md5(is);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
