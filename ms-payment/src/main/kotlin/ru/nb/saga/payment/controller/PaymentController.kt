package ru.nb.saga.payment.controller

import org.springframework.stereotype.Controller
import ru.nb.saga.common.Log
import ru.nb.saga.common.kafka.BaseConsumer
import ru.nb.saga.common.kafka.DataSender
import ru.nb.saga.common.model.OrderEvent
import ru.nb.saga.common.model.PaymentEvent
import ru.nb.saga.payment.data.Payment
import ru.nb.saga.payment.data.PaymentRepository

@Controller
class PaymentController(
	private val repository: PaymentRepository,
	private val dataSender: DataSender<PaymentEvent>,
	private val reverseDataSender: DataSender<OrderEvent>,
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
			dataSender.send(paymentEvent)
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
			reverseDataSender.send(oe)
		}
	}

	companion object : Log()
}
