package ru.nb.saga.orders.data

import org.springframework.data.repository.CrudRepository

interface OrderRepository : CrudRepository<OrderEntity, Long>
