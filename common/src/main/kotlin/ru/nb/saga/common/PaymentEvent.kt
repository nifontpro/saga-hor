package ru.nb.saga.common

data class PaymentEvent(
	val type: String,
	val order: CustomerOrder,
)
