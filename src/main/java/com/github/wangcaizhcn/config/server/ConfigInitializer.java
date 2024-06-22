package com.github.wangcaizhcn.config.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wangcaizhcn.config.util.FileReadUtil;

@Component
@Order(520)
public class ConfigInitializer implements ApplicationRunner {
	
	private static final String DEFAULT_CONFI_FILE_NAME = "default.conf";

	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		String path = System.getProperty("user.dir");
		File dir = new File(path + "/conf");
		if(!dir.exists()) {
			dir.mkdirs();
		}
		System.out.println("配置信息存储位置：" + dir);
		
		Map<String, List<File>> configFiles = readConfigFiles(dir);
		
		if(configFiles == null || configFiles.size() == 0) {
			File defaultDir = new File(dir, "default");
			defaultDir.mkdir();
			
			// 初始化没有任何配置文件时，将默认配置文件生成一个当前版本的配置文件
			ClassPathResource classPathResource = new ClassPathResource(DEFAULT_CONFI_FILE_NAME);
			try(InputStream fis = classPathResource.getInputStream();
					FileOutputStream fileOutputStream = new FileOutputStream(new File(defaultDir, DEFAULT_CONFI_FILE_NAME));) {
				byte[] buf = new byte[1024];
	            int readLen = 0;
	            while ((readLen = fis.read(buf)) != -1){
	                fileOutputStream.write(buf, 0, readLen);
	            }
	            System.out.println("不存在配置信息，使用内置配置信息创建配置文件：" + DEFAULT_CONFI_FILE_NAME);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			List<File> list = new ArrayList<>();
			list.add(new File(defaultDir, DEFAULT_CONFI_FILE_NAME));
			
			configFiles.put(defaultDir.getName(), list);
		}
		
		List<String> versions = new ArrayList<>();
		Map<String, List<String>> versionTags = new HashMap<>();
		Map<String, List<ConfigItemBean>> configInfos = new HashMap<>();
		
		for(Map.Entry<String, List<File>> entry : configFiles.entrySet()) {
			versions.add(entry.getKey());
			versionTags.put(entry.getKey(), new ArrayList<>());
			for(File config : entry.getValue()) {
				String fileName = config.getName();
				System.out.print("读取配置文件：" + entry.getKey() + "/" + fileName);
				try {
					String tag = fileName.substring(0, fileName.length() - 5);
					
					versionTags.get(entry.getKey()).add(tag);
					
					String info = FileReadUtil.read(config.getAbsolutePath());
					if(StringUtils.isBlank(info)) {
						configInfos.put(entry.getKey() + "_" + tag, new ArrayList<>());
					} else {
						ObjectMapper mapper = new ObjectMapper();
						List<ConfigItemBean> configs = mapper.readValue(info, new TypeReference<List<ConfigItemBean>>() {});
						configInfos.put(entry.getKey() + "_" + tag, configs);
					}
					
					ConfigModel configModel = ConfigModel.getInstance();
					configModel.setVersions(versions);
					configModel.setVersionTags(versionTags);
					configModel.setConfigInfos(configInfos);
					
					System.out.println(" ====> 成功");
					
				} catch(Exception e) {
					System.out.println(" ====> 失败");
				}
			}
		}
		
		System.out.println("加载配置文件完成。");
	}
	
	/**
	 * 读取全部配置文件
	 * @param basePath
	 * @return
	 */
	private Map<String, List<File>> readConfigFiles(File dir) {
		
		Map<String, List<File>> configs = new LinkedHashMap<>();
		
		// 第一层是文件夹，对应版本
		List<File> versionList = new ArrayList<>();
		for(File versionDir : dir.listFiles()) {
			if(versionDir.isDirectory()) {
				if(versionDir.getName().indexOf("_") > -1) {
					System.out.println(dir.getAbsolutePath() + "的【" + versionDir.getName() + "】版本的配置文件忽略读取，原因：包含下划线（_），可能被手工修改。");
				} else {
					versionList.add(versionDir);
				}
			} else {
				System.out.println(dir.getAbsolutePath() + "的【" + versionDir.getName() + "】是文件，忽略读取。");
			}
		}
		// 按时间排序
		Collections.sort(versionList, (o1, o2) -> {
			return o2.lastModified() > o1.lastModified() ? 1 : -1;
		});
		
		// 读取版本下面的文件，文件名是标签名
		for(File versionDir : versionList) {
			List<File> tagList = new ArrayList<>();
			for(File tag : versionDir.listFiles()) {
				if(tag.isFile()) {
					if(tag.getName().indexOf("_") > -1) {
						System.out.println(versionDir.getAbsolutePath() + "的【" + tag.getName() + "】配置文件忽略读取，原因：包含下划线（_），可能被手工修改。");
						continue;
					}
					
					if(!tag.getName().endsWith(".conf")) {
						System.out.println(versionDir.getAbsolutePath() + "的【" + tag.getName() + "】配置文件忽略读取，原因：该文件不是配置文件类型。");
						continue;
					}
					
					tagList.add(tag);
				} else {
					System.out.println(versionDir.getAbsolutePath() + "的【" + tag.getName() + "】是文件夹，忽略读取。");
				}
			}
			
			// 按时间排序
			Collections.sort(tagList, (o1, o2) -> {
				return o2.lastModified() > o1.lastModified() ? 1 : -1;
			});
			
			if(CollectionUtils.isNotEmpty(tagList)) {
				configs.put(versionDir.getName(), tagList);
			} else {
				System.out.println(versionDir.getAbsolutePath() + "下没有配置文件。");
			}
			
		}
		
		return configs;
	}
}
