package ru.nb.saga.orders.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import ru.nb.saga.common.model.OrderEvent
import ru.nb.saga.orders.controller.ReverseOrder

@Service
class ReverseConsumerKafka(
	private val reverseOrder: ReverseOrder
) {

	@KafkaListener(
		topics = ["\${kafka.consumer.topic}"],
		containerFactory = "reverseListenerFactory",
	)
	fun listen(@Payload values: List<OrderEvent>) {
//	fun listen(@Payload value: OrderEvent) {
		values.forEach { value ->
			reverseOrder.accept(value)
		}

	}
}