package com.nmichail.meridian.core.validation.shortname

import javax.inject.Inject

class ShortNameUseCase @Inject constructor() : (String) -> String {

	override fun invoke(name: String): String = ShortNameValidator.of(name)
}
