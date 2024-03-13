package ru.nb.saga.orders.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import ru.nb.saga.common.Log
import ru.nb.saga.common.kafka.DataSender
import ru.nb.saga.common.kafka.DataSenderKafka
import ru.nb.saga.common.kafka.baseProducerProps
import ru.nb.saga.common.model.OrderEvent

@Configuration
class ProducerConfig(
	@Value("\${kafka.producer.topic}") val producerTopicName: String,
	@Value("\${kafka.bootstrap-servers}") val bootstrapServers: String,
	@Value("\${kafka.producer.client-id}") val producerClientId: String,
) {

	@Bean
	fun producerFactory(): ProducerFactory<String, OrderEvent> = DefaultKafkaProducerFactory(
		baseProducerProps(
			bootstrapServers = bootstrapServers,
			producerClientId = producerClientId
		)
	)

	@Bean
	fun kafkaTemplate(
		producerFactory: ProducerFactory<String, OrderEvent>
	): KafkaTemplate<String, OrderEvent> = KafkaTemplate(producerFactory)


	@Bean
	fun dataSender(kafkaTemplate: KafkaTemplate<String, OrderEvent>): DataSender<OrderEvent> {
		return DataSenderKafka(
			topic = producerTopicName,
			template = kafkaTemplate
		) { log.info("After send: {}", it) }
	}

	@Bean("newProducerTopic")
	fun topic(): NewTopic {
		return TopicBuilder.name(producerTopicName).partitions(1).replicas(1).build()
	}

	companion object : Log()
}