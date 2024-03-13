package ru.nb.saga.payment.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import ru.nb.saga.common.Log
import ru.nb.saga.common.model.PaymentEvent
import ru.nb.saga.payment.controller.ReversePayment

@Service
class ReverseConsumerKafka(
	private val reversePayment: ReversePayment
) {

	@KafkaListener(
		topics = ["\${kafka.consumer.reverse.topic}"],
		containerFactory = "reverseListenerFactory",
	)
	fun listen(@Payload values: List<PaymentEvent>) {
		values.forEach { value ->
			reversePayment.accept(value)
		}
	}

	companion object : Log()
}