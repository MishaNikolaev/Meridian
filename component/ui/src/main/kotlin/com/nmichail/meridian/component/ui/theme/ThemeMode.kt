package com.nmichail.meridian.component.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

private const val PREFERENCES = "meridian"
private const val THEME = "app_theme"

fun loadTheme(context: Context) {
	val stored = context.applicationContext
		.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
		.getString(THEME, ThemeChoice.System.name)

	val choice = ThemeChoice.entries.firstOrNull { mode -> mode.name == stored } ?: ThemeChoice.System

	SelectedTheme.setAppTheme(choice.toBoolean())
}

fun selectTheme(context: Context, next: ThemeChoice) {
	SelectedTheme.setAppTheme(next.toBoolean())

	context.applicationContext
		.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
		.edit()
		.putString(THEME, next.name)
		.apply()
}

@Composable
fun themeChoice(): ThemeChoice {
	val selected by SelectedTheme.darkThemeFlow.collectAsState()
	return when (selected) {
		null  -> ThemeChoice.System
		false -> ThemeChoice.Light
		true  -> ThemeChoice.Dark
	}
}