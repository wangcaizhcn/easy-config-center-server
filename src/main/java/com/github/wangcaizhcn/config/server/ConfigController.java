package com.github.wangcaizhcn.config.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wangcaizhcn.config.basic.ApiResult;
import com.github.wangcaizhcn.config.basic.ApiResultTemplate;
import com.github.wangcaizhcn.config.basic.CommonResultCodeConstant;
import com.github.wangcaizhcn.config.basic.SeasException;

@RestController
@RequestMapping("/management")
public class ConfigController {

	@GetMapping("/versions")
	public ApiResult<List<String>> getVersions() {
		ConfigModel model = ConfigModel.getInstance();
		return ApiResultTemplate.success(model.getVersions());
	}
	
	@GetMapping("/tags")
	public ApiResult<List<String>> getTags(@RequestParam String version) {
		ConfigModel model = ConfigModel.getInstance();
		return ApiResultTemplate.success(model.getVersionTags().containsKey(version) ? model.getVersionTags().get(version) : new ArrayList<>());
	}
	
	@GetMapping("/configs")
	public ApiResult<List<ConfigItemBean>> getConfig(@RequestParam String version, @RequestParam String tag) {
		ConfigModel model = ConfigModel.getInstance();
		String key = version + "_" + tag;
		return ApiResultTemplate.success(model.getConfigInfos().containsKey(key) ? model.getConfigInfos().get(key) : new ArrayList<>());
	}
	
	@PostMapping("/save")
	public ApiResult saveConfig(@RequestBody ConfigBean bean) {
		
		Assert.hasText(bean.getVersion(), "保存配置信息时，版本不能为空");
		Assert.hasText(bean.getTag(), "保存配置信息时，标签不能为空");
		Assert.isTrue(bean.getVersion().indexOf("_") < 0, "版本信息不能包含下划线（_）");
		Assert.isTrue(bean.getTag().indexOf("_") < 0, "标签信息不能包含下划线（_）");
		
		ConfigModel model = ConfigModel.getInstance();
		if(!(model.getVersionTags().containsKey(bean.getVersion()))) {
			throw new SeasException(CommonResultCodeConstant.DATA_ERROR, "配置信息保存的版本不存在。");
		}
		if(!(model.getVersionTags().get(bean.getVersion()).contains(bean.getTag()))) {
			throw new SeasException(CommonResultCodeConstant.DATA_ERROR, "配置信息保存的标签不存在。");
		}
		
//		Set<String> set = new HashSet<>();
//		for(ConfigItemBean config : bean.getConfigs()) {
//			if(set.contains(config.getKey())) {
//				throw new SeasException(CommonResultCodeConstant.DATA_ERROR, "配置项" + config.getKey() + "已存在。");
//			}
//			set.add(config.getKey());
//		}
				
		String fileName = bean.getTag() + ".conf";
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String path = System.getProperty("user.dir");
			File dir = new File(path + "/conf/" + bean.getVersion());
			if(!dir.exists()) {
				dir.mkdirs();
			}
			
			String jsonString = objectMapper.writeValueAsString(bean.getConfigs());
			try(FileOutputStream fileOutputStream = new FileOutputStream(new File(dir, fileName));) {
				fileOutputStream.write(jsonString.getBytes());
				model.getConfigInfos().put(bean.getVersion() + "_" + bean.getTag(), bean.getConfigs());
			} catch(Exception e) {
				throw new SeasException(CommonResultCodeConstant.REQUEST_ERROR, e.getMessage());
			}
		} catch (Exception e) {
			throw new SeasException(CommonResultCodeConstant.REQUEST_ERROR, e.getMessage());
		}
		
		return ApiResultTemplate.success();
	}
	
	@PostMapping("/clone-tag")
	public ApiResult cloneTag(@RequestBody CloneBean bean) {
		
		Assert.hasText(bean.getVersion(), "原始配置信息的版本不能为空");
		Assert.hasText(bean.getTag(), "原始配置信息的标签不能为空");
		Assert.hasText(bean.getCloneTag(), "复制后的标签名称必填");
		Assert.isTrue(bean.getCloneTag().indexOf("_") < 0, "复制后的标签名称不能包含下划线（_）");
		
		String path = System.getProperty("user.dir");
		path = path + "/conf/" + bean.getVersion();
		File source = new File(path, bean.getTag() + ".conf");
		if(!source.exists()) {
			throw new SeasException(CommonResultCodeConstant.DATA_ERROR, "配置信息不存在。");
		}
		
		ConfigModel model = ConfigModel.getInstance();
		if(model.getVersionTags().get(bean.getVersion()).contains(bean.getCloneTag())) {
			throw new SeasException(CommonResultCodeConstant.ARGUMENT_INVALID_ERROR, "复制后的标签名称已存在。");
		}
		
		File target = new File(path, bean.getCloneTag() + ".conf");
		copyFile(source, target);
		
		model.getVersionTags().get(bean.getVersion()).add(0, bean.getCloneTag());
		model.getConfigInfos().put(bean.getVersion() + "_" + bean.getCloneTag(), model.getConfigInfos().get(bean.getVersion() + "_" + bean.getTag()));
		
		return ApiResultTemplate.success();
	}
	
	@PostMapping("/clone-version")
	public ApiResult cloneVersion(@RequestBody CloneBean bean) {
		
		Assert.hasText(bean.getVersion(), "原始配置信息的版本不能为空");
		Assert.hasText(bean.getCloneVersion(), "复制后的版本名称必填");
		Assert.isTrue(bean.getCloneVersion().indexOf("_") < 0, "复制后的版本名称不能包含下划线（_）");
		
		String path = System.getProperty("user.dir");
		ConfigModel model = ConfigModel.getInstance();
		
		if(model.getVersions().contains(bean.getCloneVersion())) {
			throw new SeasException(CommonResultCodeConstant.ARGUMENT_INVALID_ERROR, "复制后的版本名称已存在");
		}
		
		for(String tag : model.getVersionTags().get(bean.getVersion())) {
			File source = new File(path + "/conf/" + bean.getVersion(),  tag + ".conf");
			if(!source.exists()) {
				throw new SeasException(CommonResultCodeConstant.DATA_ERROR, "配置信息不存在。版本：" + bean.getVersion() + "，标签：" + tag);
			}
		}
		
		// 创建版本
		File versionDir = new File(path + "/conf", bean.getCloneVersion());
		if(!versionDir.exists()) {
			versionDir.mkdirs();
		}
		
		model.getVersions().add(0, bean.getCloneVersion());
		model.getVersionTags().put(bean.getCloneVersion(), new ArrayList<>());
		
		List<String> sources = model.getVersionTags().get(bean.getVersion());
		Collections.reverse(sources);
		for(String tag : sources) {
			File source = new File(path + "/conf/" + bean.getVersion(), tag + ".conf");
			File target = new File(path + "/conf/" + bean.getCloneVersion(), tag + ".conf");
			copyFile(source, target);
			model.getVersionTags().get(bean.getCloneVersion()).add(0, tag);
			model.getConfigInfos().put(bean.getCloneVersion() + "_" + tag, model.getConfigInfos().get(bean.getVersion() + "_" + tag));
		}
		
		return ApiResultTemplate.success();
	}
	
	private void copyFile(File source, File dest) {
	    try(InputStream is = new FileInputStream(source);
	    		OutputStream os = new FileOutputStream(dest);) {
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } catch(Exception e) {
	    	throw new SeasException(CommonResultCodeConstant.REQUEST_ERROR, e.getMessage());
	    }
	}
}
