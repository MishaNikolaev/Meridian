package com.nmichail.meridian.core.validation.email

import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.ValidationItem

data class EmailValidationItem(
	override val data: String = "",
	override val validationState: DefaultValidationState = DefaultValidationState.Unverified,
) : ValidationItem<DefaultValidationState>()

fun EmailValidationItem.isValid(): Boolean = validationState is DefaultValidationState.Valid
