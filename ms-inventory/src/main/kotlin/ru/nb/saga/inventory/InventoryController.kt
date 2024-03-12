package ru.nb.saga.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.nb.saga.common.InventoryEvent
import ru.nb.saga.common.PaymentEvent

@RestController
class InventoryController(
	private val repository: InventoryRepository,
	private val kafkaTemplate: KafkaTemplate<String, InventoryEvent>,
	private val kafkaPaymentTemplate: KafkaTemplate<String, PaymentEvent>
) {

	@KafkaListener(topics = ["new-payments"], groupId = "payments-group")
	fun updateInventory(paymentEvent: String) {
		val p = ObjectMapper().readValue(paymentEvent, PaymentEvent::class.java)
		val order = p.order

		val event = InventoryEvent(
			type = "INVENTORY_UPDATED",
			order = order
		)
		try {
			// update stock in database
			val inventories = repository.findByItem(order.item)
			val exists = inventories.iterator().hasNext()
			if (!exists) throw Exception("Stock not available")

			inventories.forEach { i ->
				i.quantity -= order.quantity
				repository.save(i)
			}

			kafkaTemplate.send("new-inventory", event)
		} catch (e: Exception) {
			// reverse previous task

			val pe = PaymentEvent(
				order = order,
				type = "PAYMENT_REVERSED"
			)
			kafkaPaymentTemplate.send("reversed-payments", pe)
		}
	}

	@PostMapping("/inventory")
	fun addInventory(@RequestBody stock: Stock) {
		val items = repository.findByItem(stock.item)

		if (items.iterator().hasNext()) {
			items.forEach { i ->
				i.quantity += stock.quantity
				repository.save(i)
			}
		} else {
			val i = Inventory(
				item = stock.item,
				quantity = stock.quantity
			)
			repository.save(i)
		}
	}
}
