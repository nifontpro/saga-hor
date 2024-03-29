package ru.nb.saga.orders.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.nb.saga.common.Log
import ru.nb.saga.common.kafka.DataSender
import ru.nb.saga.common.model.CustomerOrder
import ru.nb.saga.common.model.OrderEvent
import ru.nb.saga.orders.data.OrderEntity
import ru.nb.saga.orders.data.OrderRepository

@RestController
class OrderController(
	private val repository: OrderRepository,
	private val dataSender: DataSender<OrderEvent>,
) {

	@PostMapping("/orders")
	fun createOrder(@RequestBody customerOrder: CustomerOrder) {
		var order = OrderEntity(
			item = customerOrder.item,
			quantity = customerOrder.quantity,
			amount = customerOrder.amount,
			status = "CREATED",
		)
		try {
			// save order in database
			order = repository.save(order)
			val newCustomerOrder = customerOrder.copy(
				orderId = order.id ?: throw Exception("Order ID is null")
			)

			// publish order created event for payment microservice to consume.
			val event = OrderEvent(
				order = newCustomerOrder,
				type = "ORDER_CREATED"
			)
			dataSender.send(event)
		} catch (e: Exception) {
			log.error(e.message)
			order.status = "FAILED"
			repository.save(order)
		}
	}

	companion object : Log()
}
