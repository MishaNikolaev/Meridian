package com.nmichail.meridian.core.validation.email

internal object EmailValidator {

	private val validEmail = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

	fun isEmpty(email: String): Boolean = email.isEmpty()

	fun isInvalid(email: String): Boolean = !email.matches(validEmail)
}
