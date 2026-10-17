package com.nmichail.meridian.core.validation.displayName

import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.ValidationItem

data class DisplayNameValidationItem(
	override val data: String = "",
	override val validationState: DefaultValidationState = DefaultValidationState.Unverified,
) : ValidationItem<DefaultValidationState>()

fun DisplayNameValidationItem.isValid(): Boolean = validationState is DefaultValidationState.Valid
