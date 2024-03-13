package ru.nb.saga.payment.config.consumer

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import ru.nb.saga.common.kafka.baseConsumerFactory
import ru.nb.saga.common.kafka.baseConsumerProps
import ru.nb.saga.common.model.PaymentEvent

@Configuration
class ReverseConsumerConfig(
	@Value("\${kafka.bootstrap-servers}") val bootstrapServers: String,
	@Value("\${kafka.consumer.client-id}") val consumerClientId: String,
	@Value("\${kafka.consumer.group-id}") val consumerGroupId: String,
	@Value("\${kafka.consumer.reverse.topic}") val consumerReversTopicName: String,
) {

	@Bean("reverseConsumerFactory")
	fun consumerFactory(): ConsumerFactory<String, PaymentEvent> = DefaultKafkaConsumerFactory(
		baseConsumerProps(
			bootstrapServers = bootstrapServers,
			consumerClientId = consumerClientId,
			consumerGroupId = consumerGroupId,
			packages = "ru.nb.saga.common.model.PaymentEvent:ru.nb.saga.common.model.PaymentEvent"
		)
	)

	@Bean("reverseListenerFactory")
	fun listenerContainerFactory(consumerFactory: ConsumerFactory<String, PaymentEvent>) =
		ConcurrentKafkaListenerContainerFactory<String, PaymentEvent>().also {
			baseConsumerFactory(it, consumerFactory)
		}

	@Bean("newReverseConsumerTopic")
	fun topic(): NewTopic {
		return TopicBuilder.name(consumerReversTopicName).partitions(1).replicas(1).build()
	}
}