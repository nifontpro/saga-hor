package ru.nb.saga.common.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import ru.nb.saga.common.Log

interface DataSender<T> {
	fun send(value: T)
}

class DataSenderKafka<T>(
	private val topic: String,
	private val template: KafkaTemplate<String, T>,
	private val onComplete: (T) -> Unit = {}
) : DataSender<T> {

	override fun send(value: T) {
		try {
			log.info("value: {}", value)

			/**
			 * Отправка асинхронная, но если
			 * ожидание подключения и др., то метод блокируется
			 */
			template.send(topic, value)
				.whenComplete { result: SendResult<String, T>, ex: Throwable? ->
					if (ex == null) {
						log.info(
							"Message: {} was sent, offset:{}",
							value,
							result.recordMetadata.offset()
						)
						onComplete(value)
					} else {
						log.error("Message: {} was not sent", value, ex)
					}
				}
		} catch (ex: Exception) {
			log.error("Send error, value:{}", value, ex)
		}
	}

	companion object : Log()
}