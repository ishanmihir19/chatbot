package com.ishanmihir19.chatbot.models;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class LoadBotsConfig {

	@Bean
	static LoadBots registerBeans(Environment env) {
		return new LoadBots(env);
	}

}
