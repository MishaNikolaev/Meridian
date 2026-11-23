package com.nmichail.meridian.core.validation.capitalize

import javax.inject.Inject

class CapitalizeUseCase @Inject constructor() : (String) -> String {

	override fun invoke(value: String): String = CapitalizeValidator.of(value)
}
