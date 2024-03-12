package ru.nb.saga.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class Log {
	protected val log: Logger = LoggerFactory.getLogger(javaClass.enclosingClass)
}
