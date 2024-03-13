package ru.nb.saga.common.model

data class OrderEvent (
	val type: String,
	val order: CustomerOrder,
)
