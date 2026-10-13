package com.nmichail.meridian.core.validation

import javax.inject.Inject

class NotEmptyValidator @Inject constructor() {

	fun validate(value: String): String? =
		if (value.isBlank()) {
			"Я потом норм валидаторы через юскейсы сделаю"
		} else {
			null
		}
}
