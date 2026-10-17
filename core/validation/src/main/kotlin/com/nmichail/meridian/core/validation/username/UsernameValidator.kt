package com.nmichail.meridian.core.validation.username

internal object UsernameValidator {

	private const val MIN_LENGTH = 3
	private const val MAX_LENGTH = 32
	private val allowedCharacters = Regex("^[A-Za-z0-9._-]+$")

	fun isLengthInvalid(username: String): Boolean = username.length !in MIN_LENGTH..MAX_LENGTH

	fun isCharactersInvalid(username: String): Boolean = !username.matches(allowedCharacters)
}
