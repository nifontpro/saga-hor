package ru.nb.saga.common.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor

fun baseProducerProps(
	bootstrapServers: String,
	producerClientId: String
): MutableMap<String, Any> {
	val props = mutableMapOf<String, Any>()
	props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
	props[ProducerConfig.CLIENT_ID_CONFIG] = producerClientId
	props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
	props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
	return props
}

fun baseConsumerProps(
	bootstrapServers: String,
	consumerClientId: String,
	consumerGroupId: String,
	packages: String,
): MutableMap<String, Any> {
	val props = mutableMapOf<String, Any>()
	props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
	props[ConsumerConfig.CLIENT_ID_CONFIG] = consumerClientId
	props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
	props[ConsumerConfig.GROUP_ID_CONFIG] = consumerGroupId

	props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
	props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
	props[JsonDeserializer.TYPE_MAPPINGS] = packages

		// Размер пакета сообщений, прочитанного за раз
		props[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 3

		// Максимальный интервал между двумя операциями чтения,
		// Если проходит больше этого времени, то считается, что consumer мертв
		props[ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG] = 3000
	return props
}

fun <T> baseConsumerFactory(
	containerFactory: ConcurrentKafkaListenerContainerFactory<String, T>,
	consumerFactory: ConsumerFactory<String, T>,
	taskName: String = "consumer-"
) {
	containerFactory.consumerFactory = consumerFactory
	containerFactory.isBatchListener = true
	containerFactory.setConcurrency(1)
	containerFactory.containerProperties.idleBetweenPolls = 1000

	// Подождать в магазине, пока хлеб привезут
	containerFactory.containerProperties.pollTimeout = 1000

	val executor = SimpleAsyncTaskExecutor(taskName)
	executor.concurrencyLimit = 10
	val listenerTaskExecutor = ConcurrentTaskExecutor(executor)
	containerFactory.containerProperties.listenerTaskExecutor = listenerTaskExecutor
}