package ru.nb.saga.payment.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import ru.nb.saga.common.Log
import ru.nb.saga.common.model.OrderEvent
import ru.nb.saga.payment.controller.PaymentController

@Service
class ConsumerKafka(
	private val paymentController: PaymentController,
) {

	@KafkaListener(
		topics = ["\${kafka.consumer.topic}"],
		containerFactory = "listenerContainerFactory",
	)
	fun listen(@Payload values: List<OrderEvent>) {
		values.forEach { value ->
			paymentController.accept(value)
		}
	}

	companion object : Log()
}