package ru.nb.saga.inventory.data

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Inventory(
	@Id
	@GeneratedValue
	var id: Long? = null,

	var quantity: Int,
	var item: String,
)
