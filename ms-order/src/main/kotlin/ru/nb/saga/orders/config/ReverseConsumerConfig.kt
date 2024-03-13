package ru.nb.saga.orders.config

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
import ru.nb.saga.common.model.OrderEvent

@Configuration
class ReverseConsumerConfig(
	@Value("\${kafka.consumer.topic}") val consumerTopicName: String,
	@Value("\${kafka.bootstrap-servers}") val bootstrapServers: String,
	@Value("\${kafka.consumer.client-id}") val consumerClientId: String,
	@Value("\${kafka.consumer.group-id}") val consumerGroupId: String,
) {

	@Bean
	fun consumerFactory(): ConsumerFactory<String, OrderEvent> = DefaultKafkaConsumerFactory(
		baseConsumerProps(
			bootstrapServers = bootstrapServers,
			consumerClientId = consumerClientId,
			consumerGroupId = consumerGroupId,
			packages = "ru.nb.saga.common.model.OrderEvent:ru.nb.saga.common.model.OrderEvent"
		)
	)

	@Bean("reverseListenerFactory")
	fun listenerContainerFactory(consumerFactory: ConsumerFactory<String, OrderEvent>) =
		ConcurrentKafkaListenerContainerFactory<String, OrderEvent>().also {
			baseConsumerFactory(it, consumerFactory)
		}

	@Bean("new-consumer-topic")
	fun topic(): NewTopic {
		return TopicBuilder.name(consumerTopicName).partitions(1).replicas(1).build()
	}

}
