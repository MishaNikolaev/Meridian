package com.nmichail.meridian.core.validation.initials

object InitialsValidator {

	fun of(name: String): String {
		val parts = name.trim().split(" ").filter { part -> part.isNotEmpty() }
		val letters = if (parts.size > 1) {
			parts[0].take(1) + parts[1].take(1)
		} else {
			name.take(2)
		}
		return letters.uppercase()
	}
}
