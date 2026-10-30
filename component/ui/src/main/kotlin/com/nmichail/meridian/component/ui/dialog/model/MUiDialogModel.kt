package com.nmichail.meridian.component.ui.dialog.model

enum class MUiDialogType {
	Basic,
	Icon,
	Destructive,
	List,
	Input,
}

data class MUiDialogModel(
	val type: MUiDialogType,
	val title: String,
	val message: String,
	val cancel: String,
	val confirm: String,
	val options: List<String>,
	val selectedOption: Int,
	val fieldLabel: String,
	val fieldValue: String,
)