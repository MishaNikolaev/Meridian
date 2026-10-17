package com.nmichail.meridian.component.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LocalMeridianPalette = staticCompositionLocalOf { LightPalette }

object MeridianThemeTokens {
	val palette: MeridianPalette
		@Composable get() = LocalMeridianPalette.current
}

@Composable
fun MeridianTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit,
) {
	val palette = if (darkTheme) DarkPalette else LightPalette
	SystemBarsEffect(darkTheme)
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

@Composable
private fun SystemBarsEffect(darkTheme: Boolean) {
	val view = LocalView.current
	val lightIcons = !darkTheme
	SideEffect {
		if (view.isInEditMode) {
			return@SideEffect
		}
		val window = view.context.activity()?.window ?: return@SideEffect
		WindowCompat.getInsetsController(window, view).apply {
			isAppearanceLightStatusBars = lightIcons
			isAppearanceLightNavigationBars = lightIcons
		}
	}
}

private tailrec fun Context.activity(): Activity? = when (this) {
	is Activity -> this
	is ContextWrapper -> baseContext.activity()
	else -> null
}