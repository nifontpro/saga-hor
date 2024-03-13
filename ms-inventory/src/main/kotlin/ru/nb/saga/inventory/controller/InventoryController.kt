package ru.nb.saga.inventory.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.nb.saga.common.kafka.BaseConsumer
import ru.nb.saga.common.kafka.DataSender
import ru.nb.saga.common.model.PaymentEvent
import ru.nb.saga.inventory.data.Inventory
import ru.nb.saga.inventory.data.InventoryRepository
import ru.nb.saga.inventory.data.Stock

@RestController
class InventoryController(
	private val repository: InventoryRepository,
	private val reverseDataSender: DataSender<PaymentEvent>,
) : BaseConsumer<PaymentEvent> {

	override fun accept(value: PaymentEvent) {
		val order = value.order
		try {
			// update stock in database
			val inventories = repository.findByItem(order.item)
			val exists = inventories.iterator().hasNext()
			if (!exists) throw Exception("Stock not available")

			inventories.forEach { i ->
				i.quantity -= order.quantity
				repository.save(i)
			}
		} catch (e: Exception) {
			// reverse previous task

			val paymentEvent = PaymentEvent(
				order = order,
				type = "PAYMENT_REVERSED"
			)
			reverseDataSender.send(paymentEvent)
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
