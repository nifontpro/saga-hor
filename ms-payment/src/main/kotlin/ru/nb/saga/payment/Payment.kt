package ru.nb.saga.payment

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Payment(
	@Id
	@GeneratedValue
	var id: Long? = null,
	var mode: String? = null,
	var orderId: Long,
	var amount: Double = 0.0,
	var status: String? = null,
)
