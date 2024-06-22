package com.github.wangcaizhcn.config.auth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class KeyStoreModel {

	private static volatile KeyStoreModel instance;
	
	private Map<String, String> keyMap = new HashMap<>();
	
	private Map<String, Long> keyTimeMap = new LinkedHashMap<>();
	
	private KeyStoreModel() {
		(new Listener()).start();
	}
	
	public static KeyStoreModel getInstance() {
		if(instance == null) {
			synchronized (KeyStoreModel.class) {
				if(instance == null) {
					instance = new KeyStoreModel();
				}
			}
		}
		return instance;
	}
	
	public void addKey(String key, String encodeKey) {
		this.keyMap.put(key, encodeKey);
		this.keyTimeMap.put(key, System.currentTimeMillis());
	}
	
	private void deleteKey(String key) {
		this.keyMap.remove(key);
		this.keyTimeMap.remove(key);
	}

	public String getEncodeKey(String key) {
		String encodeKey = this.keyMap.get(key);
		deleteKey(key);
		return encodeKey;
	}
	
	class Listener extends Thread {
		
		int errorCount = 0;
		
		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(10000L);
				} catch(Exception e) {
					// 防止产生意外，死循环，耗死服务
					errorCount ++;
					e.printStackTrace();
					if(errorCount > 1000) {
						System.out.println("监控线程终止");
						break;
					}
				}
				
				// TODO 这边在遍历的时候， 如果map有新增或者删除，可能会抛异常
				// TODO 这个地方可以那到外部类来处理，外部类处理时，可以加锁
				long now = System.currentTimeMillis();
				Set<String> deleteKeys = new HashSet<>();
				
				// 先标记删除的
				for(Map.Entry<String, Long> entry : (KeyStoreModel.this.keyTimeMap).entrySet()) {
					if(now - entry.getValue() > 5 * 60 * 1000L) {
						deleteKeys.add(entry.getKey());
					}
				}
				
				// 执行删除
				for(String key : deleteKeys) {
					KeyStoreModel.this.deleteKey(key);
				}
			}
		}
	}
}
