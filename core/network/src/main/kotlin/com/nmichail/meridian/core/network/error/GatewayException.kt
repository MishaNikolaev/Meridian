package com.nmichail.meridian.core.network.error

import java.io.IOException

class GatewayException(
	val code: String,
	message: String,
) : Exception(message)

fun Throwable.userMessage(): String =
	when (this) {
		is java.util.concurrent.CancellationException -> throw this
		is GatewayException -> displayMessage()
		is IOException -> "Нет соединения с сервером"
		else -> message ?: "Не получилось выполнить запрос"
	}

fun GatewayException.displayMessage(): String = messages[code] ?: message.orEmpty()

private val messages = mapOf(
	"USERNAME_TAKEN" to "Логин уже занят",
	"DEVICE_TAKEN" to "Это устройство уже привязано к аккаунту",
	"USER_NOT_FOUND" to "Пользователь не зарегистрирован",
	"DEVICE_NOT_FOUND" to "Устройство не зарегистрировано",
	"FORBIDDEN" to "Устройство отозвано",
	"PHRASE_EXPIRED" to "Проверочная строка истекла",
	"INVALID_SIGNATURE" to "Подпись не подошла",
	"PHRASE_NOT_FOUND" to "Проверочная строка не найдена",
	"UNAUTHORIZED" to "Сессия больше не активна",
	"TOO_MANY_REQUESTS" to "Слишком много попыток",
	"INVALID_USERNAME" to "Логин: 3–32 символа, буквы, цифры, точка, _ или -",
	"INVALID_DISPLAY_NAME" to "Имя: 1–128 символов",
	"INVALID_IDENTITY_KEY" to "Публичный ключ устройства не подошёл",
	"INVALID_SIGNED_PREKEY" to "Подпись ключа устройства не совпала",
	"INVALID_PREKEYS" to "Нужен хотя бы один одноразовый prekey",
	"INVALID_PREKEY" to "У одноразового prekey нет публичного ключа",
	"INVALID_ID" to "Некорректный идентификатор",
	"INVALID_PASSWORD" to "Пароль: от 8 до 128 символов",
	"PASSWORD_NOT_SET" to "Пароль для аккаунта не задан",
	"PASSWORD_MISMATCH" to "Неверный логин или пароль",
	"SERVER_NOT_FOUND" to "Сервер не найден. Проверьте адрес и подключение",
	"INVALID_HOST" to "Сервер не найден. Проверьте адрес и подключение",
	"NOT_ON_SERVER" to "Сервер или почта не найден",
)
