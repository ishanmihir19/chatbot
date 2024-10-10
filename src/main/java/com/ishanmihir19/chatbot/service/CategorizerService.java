package com.ishanmihir19.chatbot.service;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.ishanmihir19.chatbot.utils.ChatbotConstants;

import opennlp.tools.doccat.DoccatModel;

@Service
public class CategorizerService {

	@Autowired
	ApplicationContext context;

    public String categorizeSentence(String text, String botName) {
    	
    	DoccatModel model;
    	try {
			model = context.getBean(botName, DoccatModel.class);
		} catch (NoSuchBeanDefinitionException e) {
			throw new IllegalArgumentException(ChatbotConstants.NO_CHATBOT_AVAILABLE + botName);
		}


        return null;
    }

}
