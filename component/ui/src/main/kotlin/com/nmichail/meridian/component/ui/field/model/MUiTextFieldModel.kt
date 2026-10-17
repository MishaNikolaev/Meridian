package com.nmichail.meridian.component.ui.field.model

enum class MUiFieldIcon {
	None,
	Mail,
	Search,
	Building,
}

data class MUiTextFieldModel(
	val icon: MUiFieldIcon,
	val value: String,
	val placeholder: String = "",
	val error: String = "",
	val label: String = "",
	val secret: Boolean = false,
	val email: Boolean = false,
	val highlighted: Boolean = false,
)
