package ru.nb.saga.common.kafka

interface BaseConsumer<T> {
	fun accept(value: T)
}