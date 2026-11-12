package com.nmichail.meridian.component.ui.initials

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.nmichail.meridian.component.ui.theme.palette

@Composable
fun markColor(name: String): Color {
	val colors = listOf(
		palette.statusSuccess,
		palette.accentExternal,
		palette.accentPrimary,
	)

	return colors[name.hashCode().mod(colors.size)]
}