package com.nmichail.meridian.component.ui.tab.model

data class MUiTabModel(
	val label: String,
	val selected: Boolean,
)

data class MUiTitleSwitchModel(
	val leadingTitle: String,
	val leadingCount: Int,
	val trailingTitle: String,
	val trailingCount: Int,
	val leadingSelected: Boolean,
)
