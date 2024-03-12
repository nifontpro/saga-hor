package ru.nb.saga.common

data class InventoryEvent(
	val type: String,
	val order: CustomerOrder
)
