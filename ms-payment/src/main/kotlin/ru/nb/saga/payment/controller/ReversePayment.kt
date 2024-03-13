package ru.nb.saga.payment.controller

import org.springframework.stereotype.Component
import ru.nb.saga.common.Log
import ru.nb.saga.common.kafka.BaseConsumer
import ru.nb.saga.common.kafka.DataSender
import ru.nb.saga.common.model.OrderEvent
import ru.nb.saga.common.model.PaymentEvent
import ru.nb.saga.payment.data.PaymentRepository

@Component
class ReversePayment(
	private val repository: PaymentRepository,
	private val reverseDataSender: DataSender<OrderEvent>,
) : BaseConsumer<PaymentEvent> {

	override fun accept(value: PaymentEvent) {
		try {
			val order = value.order

			// do refund..
			// update status as failed
			val payments = repository.findByOrderId(order.orderId)

			payments.forEach { p ->
				p.status = "FAILED"
				repository.save(p)
			}

			// reverse previous task
			val orderEvent = OrderEvent(
				order = order,
				type = "ORDER_REVERSED"
			)
			reverseDataSender.send(orderEvent)
		} catch (e: Exception) {
			log.error(e.message)
		}
	}

	companion object : Log()
}
