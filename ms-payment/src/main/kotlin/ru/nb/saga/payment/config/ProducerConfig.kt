package ru.nb.saga.payment.config

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import ru.nb.saga.common.OrderEvent

@Configuration
class ProducerConfig(
	@Value("\${kafka.producer.reverse.topic}") val producerReverseTopicName: String,
	@Value("\${kafka.bootstrap-servers}") val bootstrapServers: String,
	@Value("\${kafka.producer.client-id}") val producerClientId: String,
) {

	@Bean
	fun producerFactory(): ProducerFactory<String, OrderEvent> {
		val props = mutableMapOf<String, Any>()
		props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
		props[ProducerConfig.CLIENT_ID_CONFIG] = producerClientId
		props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
		props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
		return DefaultKafkaProducerFactory(props)
	}

	@Bean
	fun kafkaTemplate(
		producerFactory: ProducerFactory<String, OrderEvent>
	): KafkaTemplate<String, OrderEvent> {
		return KafkaTemplate(producerFactory)
	}

	@Bean("new-producer-topic")
	fun topic(): NewTopic {
		return TopicBuilder.name(producerReverseTopicName).partitions(1).replicas(1).build()
	}
}