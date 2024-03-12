package ru.nb.saga.payment.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import ru.nb.saga.common.OrderEvent
import ru.nb.saga.common.PaymentEvent

@Configuration
class ReverseConsumerConfig(
	@Value("\${kafka.bootstrap-servers}") val bootstrapServers: String,
	@Value("\${kafka.consumer.client-id}") val consumerClientId: String,
	@Value("\${kafka.consumer.group-id}") val consumerGroupId: String,
) {

	@Bean("reverse-consumer-factory")
	fun consumerFactory(): ConsumerFactory<String, PaymentEvent> {
		return DefaultKafkaConsumerFactory(
			baseConsumerProps(
				bootstrapServers = bootstrapServers,
				consumerClientId = consumerClientId,
				consumerGroupId = consumerGroupId,
				packages = "ru.nb.saga.common.PaymentEvent:ru.nb.saga.common.PaymentEvent"
			)
		)
	}

	@Bean(KAFKA_REVERSE_CONTAINER_FACTORY)
	fun listenerContainerFactory(
		consumerFactory: ConsumerFactory<String, PaymentEvent>
	): ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> {
		return ConcurrentKafkaListenerContainerFactory<String, PaymentEvent>().also {
			baseConsumerFactory(it, consumerFactory)
		}
	}

}

const val KAFKA_REVERSE_CONTAINER_FACTORY = "reverseContainerFactory"