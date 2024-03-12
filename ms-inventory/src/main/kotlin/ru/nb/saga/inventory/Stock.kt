package ru.nb.saga.inventory

data class Stock (
	var item: String,
	var quantity: Int = 0,
)
