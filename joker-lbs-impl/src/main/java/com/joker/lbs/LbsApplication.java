package com.joker.lbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("com.joker.lbs")
@Configuration
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class LbsApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(LbsApplication.class, args);
		System.out.println("项目已启动！");
	}
}
