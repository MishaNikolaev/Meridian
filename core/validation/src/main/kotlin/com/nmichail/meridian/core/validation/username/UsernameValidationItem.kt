package com.nmichail.meridian.core.validation.username

import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.ValidationItem

data class UsernameValidationItem(
	override val data: String = "",
	override val validationState: DefaultValidationState = DefaultValidationState.Unverified,
) : ValidationItem<DefaultValidationState>()

fun UsernameValidationItem.isValid(): Boolean = validationState is DefaultValidationState.Valid
