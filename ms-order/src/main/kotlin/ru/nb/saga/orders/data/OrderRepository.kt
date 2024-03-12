package ru.nb.saga.orders.data

import org.springframework.data.repository.CrudRepository
import ru.nb.saga.orders.data.OrderEntity

interface OrderRepository : CrudRepository<OrderEntity, Long>
