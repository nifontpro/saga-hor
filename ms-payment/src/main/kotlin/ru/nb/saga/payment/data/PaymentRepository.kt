package ru.nb.saga.payment.data

import org.springframework.data.repository.CrudRepository

interface PaymentRepository : CrudRepository<Payment, Long> {
	fun findByOrderId(orderId: Long): List<Payment>
}
