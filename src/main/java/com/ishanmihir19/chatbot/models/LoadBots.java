package com.ishanmihir19.chatbot.models;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import com.ishanmihir19.chatbot.utils.ChatbotConstants;

import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;

public class LoadBots implements BeanDefinitionRegistryPostProcessor {

	@Autowired
	ApplicationContext context;

	public static final String PROPERTIES_STRING = "bot.names";
	private final List<String> bots;

	public LoadBots(Environment env) {

		bots = Binder.get(env).bind(PROPERTIES_STRING, Bindable.listOf(String.class))
				.orElseThrow(IllegalStateException::new);

	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] { new BagOfWordsFeatureGenerator() });

		TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
		params.put(TrainingParameters.CUTOFF_PARAM, 0);
		for (String bot : bots) {
			try {
				File file = new File(ChatbotConstants.BOT_FOLDER + bot + ChatbotConstants.TXT);
				InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(
						new File(ChatbotConstants.BOT_FOLDER + bot + ChatbotConstants.TXT));
				ObjectStream<String> lineStream;

				lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);

				ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
				DoccatModel doccatModel = DocumentCategorizerME.train(ChatbotConstants.LANGUAGE_CODE_ENGLISH,
						sampleStream, params, factory);

				GenericBeanDefinition gbd = new GenericBeanDefinition();
				gbd.setBeanClass(DoccatModel.class);
				gbd.setInstanceSupplier(() -> doccatModel);
				registry.registerBeanDefinition(bot, gbd);
			} catch (Exception e) {
				throw new IllegalArgumentException(ChatbotConstants.NO_CHATBOT_AVAILABLE + bot);
			}
		}

	}

}
