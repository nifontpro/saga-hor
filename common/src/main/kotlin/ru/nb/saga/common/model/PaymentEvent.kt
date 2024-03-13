package ru.nb.saga.common.model

data class PaymentEvent(
	val type: String,
	val order: CustomerOrder,
)
