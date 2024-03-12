package ru.nb.saga.payment.controller

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Controller
import ru.nb.saga.common.BaseConsumer
import ru.nb.saga.common.Log
import ru.nb.saga.common.OrderEvent
import ru.nb.saga.common.PaymentEvent
import ru.nb.saga.payment.data.Payment
import ru.nb.saga.payment.data.PaymentRepository

@Controller
class PaymentController(
	private val repository: PaymentRepository,
	@Qualifier("kafkaTemplate") private val kafkaTemplate: KafkaTemplate<String, PaymentEvent>,
	@Qualifier("reverseKafkaTemplate") private val reverseKafkaTemplate: KafkaTemplate<String, OrderEvent>,
	@Value("\${kafka.producer.topic}") val producerTopicName: String,
	@Value("\${kafka.producer.reverse.topic}") val producerReverseTopicName: String,
) : BaseConsumer<OrderEvent> {

	override fun accept(value: OrderEvent) {
		val order = value.order
		val payment = Payment(
			amount = order.amount,
			mode = order.paymentMode,
			orderId = order.orderId,
			status = "SUCCESS",
		)

		try {
			if (order.address.isNullOrBlank()) {
				log.info("--> A. Blank!!!")
				throw Exception("Address is blank")
			}

			repository.save(payment)

			log.info("PAYMENT_CREATED")

			// publish payment created event for inventory microservice to consume.
			val paymentEvent = PaymentEvent(
				order = value.order,
				type = "PAYMENT_CREATED"
			)
			kafkaTemplate.send(producerTopicName, paymentEvent)
		} catch (e: Exception) {
			log.error(e.message)
			payment.orderId = order.orderId
			payment.status = "FAILED"
			repository.save(payment)

			// reverse previous task
			val oe = OrderEvent(
				order = order,
				type = "ORDER_REVERSED"
			)
			reverseKafkaTemplate.send(producerReverseTopicName, oe)
		}
	}

	companion object : Log()
}
