package ru.nb.saga.shipment

import org.springframework.data.repository.CrudRepository

interface ShipmentRepository : CrudRepository<Shipment, Long>
