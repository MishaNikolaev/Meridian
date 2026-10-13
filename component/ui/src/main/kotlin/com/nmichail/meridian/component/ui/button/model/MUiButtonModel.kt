package com.nmichail.meridian.component.ui.button.model

enum class MUiButtonStyle {
	Primary,
	Secondary,
	Danger,
	Text,
	Icon,
	Fab,
}

data class MUiButtonModel(
	val style: MUiButtonStyle,
	val label: String,
)
