package ru.nb.saga.inventory

import org.springframework.data.repository.CrudRepository

interface InventoryRepository : CrudRepository<Inventory, Long> {
	fun findByItem(item: String): List<Inventory>
}
