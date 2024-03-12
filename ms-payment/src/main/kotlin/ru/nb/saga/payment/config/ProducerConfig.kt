package ru.nb.saga.payment.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import ru.nb.saga.common.OrderEvent
import ru.nb.saga.common.PaymentEvent

@Configuration
class ProducerConfig(
	@Value("\${kafka.producer.topic}") val producerTopicName: String,
	@Value("\${kafka.bootstrap-servers}") val bootstrapServers: String,
	@Value("\${kafka.producer.client-id}") val producerClientId: String,
) {

	@Bean("producer-factory")
	fun producerFactory(): ProducerFactory<String, PaymentEvent> {
		return DefaultKafkaProducerFactory(
			baseProducerProps(
				bootstrapServers = bootstrapServers,
				producerClientId = producerClientId
			)
		)
	}

	@Bean("kafkaTemplate")
	fun kafkaTemplate(
		@Qualifier("producer-factory") producerFactory: ProducerFactory<String, PaymentEvent>
	): KafkaTemplate<String, PaymentEvent> {
		return KafkaTemplate(producerFactory)
	}

	@Bean("new-producer-topic")
	fun topic(): NewTopic {
		return TopicBuilder.name(producerTopicName).partitions(1).replicas(1).build()
	}
}