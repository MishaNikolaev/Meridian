package com.nmichail.meridian.core.validation.shortname

object ShortNameValidator {

	fun of(name: String): String {
		val parts = name.trim().split(" ").filter { part -> part.isNotEmpty() }

		return if (parts.size > 2) {
			parts[0] + " " + parts[1]
		} else {
			name
		}
	}
}
