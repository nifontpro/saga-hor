package ru.nb.saga.payment.consumer

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.TopicBuilder
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import ru.nb.saga.common.Log
import ru.nb.saga.common.PaymentEvent
import ru.nb.saga.payment.config.KAFKA_REVERSE_CONTAINER_FACTORY
import ru.nb.saga.payment.controller.ReversePayment

@Service
class ReverseConsumerKafka(
	private val reversePayment: ReversePayment,
	@Value("\${kafka.consumer.reverse.topic}") val consumerReversTopicName: String,
) {

	@KafkaListener(
		topics = ["\${kafka.consumer.reverse.topic}"],
		containerFactory = KAFKA_REVERSE_CONTAINER_FACTORY,
	)
//	fun listen(@Payload values: List<OrderEvent>) {
	fun listen(@Payload value: PaymentEvent) {
		reversePayment.accept(value)
	}

	@Bean("new-reverse-consumer-topic")
	fun topic(): NewTopic {
		return TopicBuilder.name(consumerReversTopicName).partitions(1).replicas(1).build()
	}

	companion object : Log()
}