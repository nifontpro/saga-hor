package ru.nb.saga.payment.config.producer

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Qualifier
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
class ProducerConfig(
	@Value("\${kafka.producer.topic}") val producerTopicName: String,
	@Value("\${kafka.bootstrap-servers}") val bootstrapServers: String,
	@Value("\${kafka.producer.client-id}") val producerClientId: String,
) {

	@Bean("producerFactory")
	fun producerFactory(): ProducerFactory<String, PaymentEvent> = DefaultKafkaProducerFactory(
		baseProducerProps(
			bootstrapServers = bootstrapServers,
			producerClientId = producerClientId
		)
	)

	@Bean("kafkaProducer")
	fun kafkaTemplate(
		@Qualifier("producerFactory") producerFactory: ProducerFactory<String, PaymentEvent>
	): KafkaTemplate<String, PaymentEvent> = KafkaTemplate(producerFactory)

	@Bean
	fun dataSender(kafkaTemplate: KafkaTemplate<String, PaymentEvent>): DataSender<PaymentEvent> {
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