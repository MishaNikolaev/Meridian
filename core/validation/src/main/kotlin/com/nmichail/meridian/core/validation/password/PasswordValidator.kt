package com.nmichail.meridian.core.validation.password

internal object PasswordValidator {

	private const val MIN_LENGTH = 8
	private const val MAX_LENGTH = 128

	fun isLengthInvalid(password: String): Boolean = password.length !in MIN_LENGTH..MAX_LENGTH
}
