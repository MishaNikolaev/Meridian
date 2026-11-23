package com.nmichail.meridian.component.ui.theme

enum class ThemeChoice {
	System,
	Light,
	Dark,
}

fun ThemeChoice.toBoolean(): Boolean? = when (this) {
	ThemeChoice.Light -> false
	ThemeChoice.Dark -> true
	ThemeChoice.System -> null
}