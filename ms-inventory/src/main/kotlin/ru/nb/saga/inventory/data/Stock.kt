package ru.nb.saga.inventory.data

data class Stock (
	var item: String,
	var quantity: Int = 0,
)
