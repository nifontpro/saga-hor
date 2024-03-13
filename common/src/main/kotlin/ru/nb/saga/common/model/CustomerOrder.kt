package ru.nb.saga.common.model

data class CustomerOrder(
	val orderId: Long = 0,
	val item: String = "",
	val quantity: Int = 0,
	val amount: Double = 0.0,
	val paymentMode: String? = null,
	val address: String? = null,
)
