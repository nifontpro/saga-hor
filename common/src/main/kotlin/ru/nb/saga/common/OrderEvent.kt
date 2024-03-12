package ru.nb.saga.common

data class OrderEvent (
	val type: String,
	val order: CustomerOrder,
)
