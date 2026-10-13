package com.nmichail.meridian.component.ui.tab.model

data class MUiTabModel(
	val label: String,
	val selected: Boolean,
)

data class MUiTitleSwitchModel(
	val leadingTitle: String,
	val leadingCount: String,
	val trailingTitle: String,
	val trailingCount: String,
	val leadingSelected: Boolean,
)
