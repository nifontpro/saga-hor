package ru.nb.saga.orders.controller

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import ru.nb.saga.common.kafka.BaseConsumer
import ru.nb.saga.common.Log
import ru.nb.saga.common.model.OrderEvent
import ru.nb.saga.orders.data.OrderRepository

@Component
class ReverseOrder(
	private val repository: OrderRepository
) : BaseConsumer<OrderEvent> {

	override fun accept(value: OrderEvent) {
		log.info("--> ORDER REVERSE!!!")
		try {
			val order = repository.findByIdOrNull(value.order.orderId) ?: run {
				throw Exception("Order ${value.order.orderId} Not found")
			}
			order.status = "FAILED"
			repository.save(order)
		} catch (e: Exception) {
			log.error(e.message)
		}
	}

	companion object : Log()
}
