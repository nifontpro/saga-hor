package ru.nb.saga.payment.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import ru.nb.saga.common.OrderEvent

@Configuration
class ConsumerConfig(
	@Value("\${kafka.bootstrap-servers}") val bootstrapServers: String,
	@Value("\${kafka.consumer.client-id}") val consumerClientId: String,
	@Value("\${kafka.consumer.group-id}") val consumerGroupId: String,
) {

	@Bean("consumer-factory")
	fun consumerFactory(): ConsumerFactory<String, OrderEvent> {
		return DefaultKafkaConsumerFactory(
			baseConsumerProps(
				bootstrapServers = bootstrapServers,
				consumerClientId = consumerClientId,
				consumerGroupId = consumerGroupId,
				packages = "ru.nb.saga.common.OrderEvent:ru.nb.saga.common.OrderEvent"
			)
		)
	}

	@Bean(KAFKA_LISTENER_CONTAINER_FACTORY)
	fun listenerContainerFactory(
		consumerFactory: ConsumerFactory<String, OrderEvent>
	): ConcurrentKafkaListenerContainerFactory<String, OrderEvent> {
		return ConcurrentKafkaListenerContainerFactory<String, OrderEvent>().also {
			baseConsumerFactory(it, consumerFactory)
		}
	}

}

const val KAFKA_LISTENER_CONTAINER_FACTORY = "listenerContainerFactory"