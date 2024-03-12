package ru.nb.saga.common

interface BaseConsumer<T> {
	fun accept(value: T)
}