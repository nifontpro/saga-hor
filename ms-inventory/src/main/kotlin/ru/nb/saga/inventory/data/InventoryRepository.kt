package ru.nb.saga.inventory.data

import org.springframework.data.repository.CrudRepository
import ru.nb.saga.inventory.data.Inventory

interface InventoryRepository : CrudRepository<Inventory, Long> {
	fun findByItem(item: String): List<Inventory>
}
