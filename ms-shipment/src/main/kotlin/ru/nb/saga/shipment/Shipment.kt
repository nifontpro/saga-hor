package ru.nb.saga.shipment

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Shipment(
	@Id
	@GeneratedValue
	var id: Long? = null,

	var address: String?,
	var status: String,
	var orderId: Long,
)
