package com.nmichail.meridian.core.validation.initials

import javax.inject.Inject

class InitialsUseCase @Inject constructor() : (String) -> String {

	override fun invoke(name: String): String = InitialsValidator.of(name)
}