package com.joker.lbs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@ImportResource("classpath:dubbo-provider.xml")
@Configuration
public class DubboServiceConfig {
}
