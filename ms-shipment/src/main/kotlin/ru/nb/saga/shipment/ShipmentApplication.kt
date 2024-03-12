package ru.nb.saga.shipment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShipmentApplication

fun main(args: Array<String>) {
	runApplication<ShipmentApplication>(*args)
}