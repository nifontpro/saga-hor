package ru.nb.saga.payment

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import ru.nb.saga.common.Log
import ru.nb.saga.common.OrderEvent
import ru.nb.saga.common.PaymentEvent

@Component
class ReversePayment(
	private val repository: PaymentRepository,
	private val kafkaTemplate: KafkaTemplate<String, OrderEvent>,
) {

	@KafkaListener(topics = ["reversed-payments"], groupId = "payments-group")
	fun reversePayment(event: String) {
		try {
			val paymentEvent = ObjectMapper().readValue(event, PaymentEvent::class.java)
			val order = paymentEvent.order

			// do refund..
			// update status as failed
			val payments = repository.findByOrderId(order.orderId)

			payments.forEach { p ->
				p.status = "FAILED"
				repository.save(p)
			}

			// reverse previous task
			val orderEvent = OrderEvent(
				order = paymentEvent.order,
				type = "ORDER_REVERSED"
			)
			kafkaTemplate.send("reversed-orders", orderEvent)
		} catch (e: Exception) {
			log.error(e.message)
		}
	}

	companion object : Log()
}
