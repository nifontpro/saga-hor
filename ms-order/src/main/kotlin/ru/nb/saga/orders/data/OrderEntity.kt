package ru.nb.saga.orders.data

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class OrderEntity(
	@Id
	@GeneratedValue
	var id: Long? = null,

	var item: String = "",
	var quantity: Int = 0,
	var amount: Double = 0.0,
	var status: String? = null,
)
