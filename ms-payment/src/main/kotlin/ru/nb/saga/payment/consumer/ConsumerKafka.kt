package ru.nb.saga.payment.consumer

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.TopicBuilder
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import ru.nb.saga.common.Log
import ru.nb.saga.common.OrderEvent
import ru.nb.saga.payment.config.KAFKA_LISTENER_CONTAINER_FACTORY
import ru.nb.saga.payment.controller.PaymentController

@Service
class ConsumerKafka(
	private val reverseOrder: PaymentController,
	@Value("\${kafka.consumer.topic}") val consumerTopicName: String,
) {

	@KafkaListener(
		topics = ["\${kafka.consumer.topic}"],
		containerFactory = KAFKA_LISTENER_CONTAINER_FACTORY,
	)
//	fun listen(@Payload values: List<OrderEvent>) {
	fun listen(@Payload value: OrderEvent) {
		reverseOrder.accept(value)
	}

	@Bean("new-consumer-topic")
	fun topic(): NewTopic {
		return TopicBuilder.name(consumerTopicName).partitions(1).replicas(1).build()
	}

	companion object : Log()
}