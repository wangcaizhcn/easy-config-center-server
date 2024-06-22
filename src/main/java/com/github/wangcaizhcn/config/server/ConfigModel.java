package com.github.wangcaizhcn.config.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置信息，直接存储在服务内存中，不依赖于数据库
 * @author Administrator
 *
 */
public class ConfigModel {

	private static volatile ConfigModel instance;
	
	// 配置信息文件的版本内容
	private List<String> versions = new ArrayList<>();
	
	// 每个配置文件对应的版本内容， key=版本
	private Map<String, List<String>> versionTags = new HashMap<>();
	
	// 每个配置文件内容，key=文件名称，即版本_tag
	private Map<String, List<ConfigItemBean>> configInfos = new HashMap<>();
	
	private ConfigModel() {}
	
	public static ConfigModel getInstance() {
		if(instance == null) {
			synchronized (ConfigModel.class) {
				if(instance == null) {
					instance = new ConfigModel();
				}
			}
		}
		return instance;
	}

	public List<String> getVersions() {
		return versions;
	}

	public void setVersions(List<String> versions) {
		this.versions = versions;
	}

	public Map<String, List<String>> getVersionTags() {
		return versionTags;
	}

	public void setVersionTags(Map<String, List<String>> versionTags) {
		this.versionTags = versionTags;
	}

	public Map<String, List<ConfigItemBean>> getConfigInfos() {
		return configInfos;
	}

	public void setConfigInfos(Map<String, List<ConfigItemBean>> configInfos) {
		this.configInfos = configInfos;
	}
	
}
