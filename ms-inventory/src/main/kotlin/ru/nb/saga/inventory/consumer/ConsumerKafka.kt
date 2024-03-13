package ru.nb.saga.inventory.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import ru.nb.saga.common.Log
import ru.nb.saga.common.model.PaymentEvent
import ru.nb.saga.inventory.controller.InventoryController

@Service
class ConsumerKafka(
	private val inventoryController: InventoryController
) {

	@KafkaListener(
		topics = ["\${kafka.consumer.topic}"],
		containerFactory = "listenerContainerFactory",
	)
	fun listen(@Payload values: List<PaymentEvent>) {
		values.forEach { value ->
			inventoryController.accept(value)
		}
	}

	companion object : Log()
}
