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
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LocalMeridianPalette = staticCompositionLocalOf { LightPalette }

val palette: MeridianPalette
	@Composable
	@ReadOnlyComposable
	get() = LocalMeridianPalette.current

@Composable
fun MeridianTheme(
	themeState: Boolean? = SelectedTheme.getAppTheme(),
	content: @Composable () -> Unit,
) {
	val darkTheme = themeState ?: isSystemInDarkTheme()

	val colors = if (darkTheme) {
		DarkPalette
	} else {
		LightPalette
	}

	val colorScheme = if (darkTheme) {
		colors.darkScheme()
	} else {
		colors.lightScheme()
	}

	SystemBarsEffect(darkTheme)

	CompositionLocalProvider(LocalMeridianPalette provides colors) {
		MaterialTheme(
			colorScheme = colorScheme,
			typography = MeridianMaterialTypography,
			content = content,
		)
	}
}

private fun MeridianPalette.lightScheme() = lightColorScheme(
	primary = accentPrimary,
	onPrimary = textOnAccent,
	secondary = accentExternal,
	background = page,
	onBackground = textPrimary,
	surface = this.surface,
	onSurface = textPrimary,
	onSurfaceVariant = textSecondary,
	outline = borderDefault,
	error = statusError,
	errorContainer = statusErrorSoft,
)

private fun MeridianPalette.darkScheme() = darkColorScheme(
	primary = accentPrimary,
	onPrimary = textOnAccent,
	secondary = accentExternal,
	background = page,
	onBackground = textPrimary,
	surface = this.surface,
	onSurface = textPrimary,
	onSurfaceVariant = textSecondary,
	outline = borderDefault,
	error = statusError,
	errorContainer = statusErrorSoft,
)

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