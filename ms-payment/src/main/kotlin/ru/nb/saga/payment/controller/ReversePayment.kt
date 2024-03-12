package ru.nb.saga.payment.controller

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import ru.nb.saga.common.BaseConsumer
import ru.nb.saga.common.Log
import ru.nb.saga.common.OrderEvent
import ru.nb.saga.payment.data.PaymentRepository
/*
@Component
class ReversePayment(
	private val repository: PaymentRepository,
	private val kafkaTemplate: KafkaTemplate<String, OrderEvent>,
) : BaseConsumer<OrderEvent> {

	override fun accept(value: OrderEvent) {
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
			kafkaTemplate.send("reversed-orders", orderEvent)
		} catch (e: Exception) {
			log.error(e.message)
		}
	}

	companion object : Log()
}*/
