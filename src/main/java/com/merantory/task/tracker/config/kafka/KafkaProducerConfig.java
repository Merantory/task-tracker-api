package com.merantory.task.tracker.config.kafka;

import com.merantory.task.tracker.model.EmailMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

	@Value("${kafka.server}")
	private String bootstrapAddress;

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> configProperties = new HashMap<>();
		configProperties.put(
				ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				bootstrapAddress);
		configProperties.put(
				ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class);
		configProperties.put(
				ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				JsonSerializer.class);
		return configProperties;
	}

	@Bean
	public ProducerFactory<String, EmailMessage> producerEmailMessageFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public KafkaTemplate<String, EmailMessage> kafkaTemplate() {
		KafkaTemplate<String, EmailMessage> template = new KafkaTemplate<>(producerEmailMessageFactory());
		template.setMessageConverter(new StringJsonMessageConverter());
		return template;
	}
}
