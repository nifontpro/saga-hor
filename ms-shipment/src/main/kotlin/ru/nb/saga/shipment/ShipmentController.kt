package ru.nb.saga.shipment

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Controller
import ru.nb.saga.common.InventoryEvent

@Controller
class ShipmentController(
	private val repository: ShipmentRepository,
	private val kafkaTemplate: KafkaTemplate<String, InventoryEvent>
) {

	@KafkaListener(topics = ["new-inventory"], groupId = "inventory-group")
	fun shipOrder(event: String) {
		val inventoryEvent = ObjectMapper().readValue(event, InventoryEvent::class.java)
		val order = inventoryEvent.order
		val shipment = Shipment(
			address = order.address,
			orderId = order.orderId,
			status = "success"
		)
		try {
			if (order.address == null) {
				throw Exception("Address not present")
			}

			repository.save(shipment)

			// do other shipment logic

		} catch (e: Exception) {
			shipment.status = "failed"
			repository.save(shipment)

			val reverseEvent = InventoryEvent(
				type = "INVENTORY_REVERSED",
				order = order
			)

			println(order)
			kafkaTemplate.send("reversed-inventory", reverseEvent)
		}
	}
}
