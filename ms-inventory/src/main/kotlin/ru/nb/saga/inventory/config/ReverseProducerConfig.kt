package ru.nb.saga.inventory.config

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
import ru.nb.saga.common.model.PaymentEvent

@Configuration
class ReverseProducerConfig(
	@Value("\${kafka.producer.reverse.topic}") val producerReverseTopicName: String,
	@Value("\${kafka.bootstrap-servers}") val bootstrapServers: String,
	@Value("\${kafka.producer.client-id}") val producerClientId: String,
) {

	@Bean
	fun producerFactory(): ProducerFactory<String, PaymentEvent> = DefaultKafkaProducerFactory(
		baseProducerProps(
			bootstrapServers = bootstrapServers,
			producerClientId = producerClientId
		)
	)

	@Bean
	fun kafkaTemplate(
		producerFactory: ProducerFactory<String, PaymentEvent>
	): KafkaTemplate<String, PaymentEvent> = KafkaTemplate(producerFactory)

	@Bean
	fun reverseDataSender(kafkaTemplate: KafkaTemplate<String, PaymentEvent>): DataSender<PaymentEvent> {
		return DataSenderKafka(
			topic = producerReverseTopicName,
			template = kafkaTemplate
		) { log.info("After send: {}", it) }
	}

	@Bean("newReverseProducerTopic")
	fun topic(): NewTopic {
		return TopicBuilder.name(producerReverseTopicName).partitions(1).replicas(1).build()
	}

	companion object : Log()
}