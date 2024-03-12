package ru.nb.saga.orders

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import ru.nb.saga.common.Log
import ru.nb.saga.common.OrderEvent

@Component
class ReverseOrder(
	private val repository: OrderRepository
) {

	@KafkaListener(topics = ["reversed-orders"], groupId = "orders-group")
	fun reverseOrder(event: String) {
		try {
			val orderEvent = ObjectMapper().readValue(event, OrderEvent::class.java)

			val order = repository.findByIdOrNull(orderEvent.order.orderId) ?: run {
				throw Exception("Order ${orderEvent.order.orderId} Not found")
			}
			order.status = "FAILED"
			repository.save(order)
		} catch (e: Exception) {
			log.error(e.message)
		}
	}

	companion object : Log()
}
