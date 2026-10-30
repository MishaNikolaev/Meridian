package com.nmichail.meridian.core.validation.username

import com.nmichail.meridian.core.validation.DefaultValidationState

internal object UsernameValidator {

	private const val MIN_LENGTH = 3
	private const val MAX_LENGTH = 32
	private val allowedCharacters = Regex("^[A-Za-z0-9._-]+$")

	fun validate(username: String): DefaultValidationState = when {
		isLengthInvalid(username) -> DefaultValidationState.Invalid(UsernameInvalidState.INVALID_LENGTH)
		isCharactersInvalid(username) -> DefaultValidationState.Invalid(UsernameInvalidState.INVALID_CHARACTERS)
		else -> DefaultValidationState.Valid
	}

	private fun isLengthInvalid(username: String): Boolean = username.length !in MIN_LENGTH..MAX_LENGTH

	private fun isCharactersInvalid(username: String): Boolean = !username.matches(allowedCharacters)
}
