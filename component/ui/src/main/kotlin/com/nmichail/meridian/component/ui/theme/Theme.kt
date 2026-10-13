package com.nmichail.meridian.component.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalMeridianPalette = staticCompositionLocalOf { LightPalette }

internal object MeridianThemeTokens {
	val palette: MeridianPalette
		@Composable get() = LocalMeridianPalette.current
}

@Composable
fun MeridianTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit,
) {
	val palette = if (darkTheme) DarkPalette else LightPalette
	val colorScheme = if (darkTheme) {
		darkColorScheme(
			primary = palette.accentPrimary,
			onPrimary = palette.textOnAccent,
			secondary = palette.accentExternal,
			background = palette.page,
			onBackground = palette.textPrimary,
			surface = palette.surface,
			onSurface = palette.textPrimary,
			onSurfaceVariant = palette.textSecondary,
			outline = palette.borderDefault,
			error = palette.statusError,
			errorContainer = palette.statusErrorSoft,
		)
	} else {
		lightColorScheme(
			primary = palette.accentPrimary,
			onPrimary = palette.textOnAccent,
			secondary = palette.accentExternal,
			background = palette.page,
			onBackground = palette.textPrimary,
			surface = palette.surface,
			onSurface = palette.textPrimary,
			onSurfaceVariant = palette.textSecondary,
			outline = palette.borderDefault,
			error = palette.statusError,
			errorContainer = palette.statusErrorSoft,
		)
	}

	CompositionLocalProvider(LocalMeridianPalette provides palette) {
		MaterialTheme(
			colorScheme = colorScheme,
			typography = MeridianMaterialTypography,
			content = content,
		)
	}
}