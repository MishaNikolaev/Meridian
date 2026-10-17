package com.nmichail.meridian.core.validation.displayName

internal object DisplayNameValidator {

	private const val MAX_LENGTH = 128

	fun isEmpty(displayName: String): Boolean = displayName.trim().isEmpty()

	fun isTooLong(displayName: String): Boolean = displayName.trim().length > MAX_LENGTH
}
