package com.github.wangcaizhcn.config.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileReadUtil {

	/**
	 * 给定文本类型的文件路径，一次性读取出来全部内容，适用于内容不多的读取场景
	 * @param filePath
	 * @return
	 */
	public static String read(String filePath) {
		StringBuilder sb = new StringBuilder();
		try(FileReader fileReader = new FileReader(filePath);
				BufferedReader reader = new BufferedReader(fileReader);) {
            String line;
            while ((line = reader.readLine()) != null) {
            	sb.append(line);
            }
        } catch(Exception e) {
        	throw new RuntimeException(e);
        }
		return sb.toString();
	}
	
	/**
	 * 给定文本类型的文件流，一次性读取出来全部内容，适用于内容不多的读取场景。
	 * 调用者自行关闭传入的流
	 * @param is
	 * @return
	 */
	public static String read(InputStream is) {
		StringBuilder sb = new StringBuilder();
		try(InputStreamReader streamfileReader = new InputStreamReader(is);
				BufferedReader reader = new BufferedReader(streamfileReader);) {
            String line;
            while ((line = reader.readLine()) != null) {
            	sb.append(line);
            }
        } catch(Exception e) {
        	throw new RuntimeException(e);
        }
		return sb.toString();
	}
}
