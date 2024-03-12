package ru.nb.saga.payment.config

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer.TYPE_MAPPINGS
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor
import org.springframework.stereotype.Service
import ru.nb.saga.common.Log
import ru.nb.saga.common.OrderEvent
import ru.nb.saga.payment.controller.PaymentController

@Configuration
class ConsumerConfig(
	@Value("\${kafka.consumer.topic}") val consumerTopicName: String,
	@Value("\${kafka.bootstrap-servers}") val bootstrapServers: String,
	@Value("\${kafka.consumer.client-id}") val consumerClientId: String,
	@Value("\${kafka.consumer.group-id}") val consumerGroupId: String,
) {

	@Bean
	fun consumerFactory(): ConsumerFactory<String, OrderEvent> {
		val props = mutableMapOf<String, Any>()
		props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
		props[ConsumerConfig.CLIENT_ID_CONFIG] = consumerClientId
		props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
		props[ConsumerConfig.GROUP_ID_CONFIG] = consumerGroupId

		props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
		props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
		props[TYPE_MAPPINGS] = "ru.nb.saga.common:ru.nb.saga.common.OrderEvent"

		/*	// Размер пакета сообщений, прочитанного за раз
			props[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 3

			// Максимальный интервал между двумя операциями чтения,
			// Если проходит больше этого времени, то считается, что consumer мертв
			props[ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG] = 3000*/

		return DefaultKafkaConsumerFactory(props)
	}

	@Bean(KAFKA_LISTENER_CONTAINER_FACTORY)
	fun listenerContainerFactory(
		consumerFactory: ConsumerFactory<String, OrderEvent>
	): ConcurrentKafkaListenerContainerFactory<String, OrderEvent> {
		val executor = SimpleAsyncTaskExecutor("consumer-")
		executor.concurrencyLimit = 10
		val listenerTaskExecutor = ConcurrentTaskExecutor(executor)
		val factory = ConcurrentKafkaListenerContainerFactory<String, OrderEvent>().also {
			it.consumerFactory = consumerFactory
			it.isBatchListener = false
			it.setConcurrency(1)
			it.containerProperties.idleBetweenPolls = 1000

			// Подождать в магазине, пока хлеб привезут
			it.containerProperties.pollTimeout = 1000
			it.containerProperties.listenerTaskExecutor = listenerTaskExecutor
		}
		return factory
	}

	@Bean("new-consumer-topic")
	fun topic(): NewTopic {
		return TopicBuilder.name(consumerTopicName).partitions(1).replicas(1).build()
	}

}

@Service
class ConsumerKafka(
	private val reverseOrder: PaymentController
) {

	@KafkaListener(
		topics = ["\${kafka.consumer.topic}"],
		containerFactory = KAFKA_LISTENER_CONTAINER_FACTORY,
	)
//	fun listen(@Payload values: List<OrderEvent>) {
	fun listen(@Payload value: OrderEvent) {
		reverseOrder.accept(value)
	}

	companion object : Log()
}

private const val KAFKA_LISTENER_CONTAINER_FACTORY = "listenerContainerFactory"