package com.nmichail.meridian.core.validation.capitalize

import java.util.Locale

object CapitalizeValidator {

	fun of(value: String): String {
		if (value.isEmpty()) {
			return value
		}

		return value.replaceFirstChar { char ->
			if (char.isLowerCase()) {
				char.titlecase(Locale.getDefault())
			} else {
				char.toString()
			}
		}
	}
}
