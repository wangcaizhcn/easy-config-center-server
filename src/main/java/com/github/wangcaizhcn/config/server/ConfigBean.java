package com.github.wangcaizhcn.config.server;

import java.util.List;

public class ConfigBean {

	private String version;
	
	private String tag;
	
	private List<ConfigItemBean> configs;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<ConfigItemBean> getConfigs() {
		return configs;
	}

	public void setConfigs(List<ConfigItemBean> configs) {
		this.configs = configs;
	}
	
}
