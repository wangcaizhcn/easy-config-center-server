package com.github.wangcaizhcn.config.auth;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.github.wangcaizhcn.config.util.KeyGenerateUtil;

@Component
@Order(521)
public class UserInitializer implements ApplicationRunner {

	@Value("${server.password:}")
    private String password;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		String path = System.getProperty("user.dir");
		File dir = new File(path);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		File userFile = new File(path, "user.ser");
		if(!userFile.exists()) {
			String password = StringUtils.isBlank(this.password) ? KeyGenerateUtil.getRandomKey8Bit() : this.password;
			AuthService.saveUser(password, "default");
			System.out.println("初始密码====>" + password);
		}
	}

}
