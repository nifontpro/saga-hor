package ru.nb.saga.payment

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Controller
import ru.nb.saga.common.OrderEvent
import ru.nb.saga.common.PaymentEvent

@Controller
class PaymentController(
	private val repository: PaymentRepository,
	private val kafkaTemplate: KafkaTemplate<String, PaymentEvent>,
	private val kafkaOrderTemplate: KafkaTemplate<String, OrderEvent>
) {

	@KafkaListener(topics = ["new-orders"], groupId = "orders-group")
//	fun processPayment(event: String) {
	fun processPayment(orderEvent: OrderEvent) {
//		println("Received event: $event")
		println("Received event: $orderEvent")
//		val orderEvent = ObjectMapper().readValue(event, OrderEvent::class.java)

		val order = orderEvent.order
		val payment = Payment(
			amount = order.amount,
			mode = order.paymentMode,
			orderId = order.orderId,
			status = "SUCCESS",
		)

		try {
			repository.save(payment)

			// publish payment created event for inventory microservice to consume.
			val paymentEvent = PaymentEvent(
				order = orderEvent.order,
				type = "PAYMENT_CREATED"
			)
			kafkaTemplate.send("new-payments", paymentEvent)
		} catch (e: Exception) {

			payment.orderId = order.orderId
			payment.status = "FAILED"
			repository.save(payment)

			// reverse previous task
			val oe = OrderEvent(
				order = order,
				type = "ORDER_REVERSED"
			)
			kafkaOrderTemplate.send("reversed-orders", oe)
		}
	}
}
