package com.nmichail.meridian.component.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val PREFERENCES = "meridian"
private const val THEME = "app_theme"

enum class ThemeChoice {
	System,
	Light,
	Dark,
}

object SelectedTheme {

	private val darkTheme: MutableState<Boolean?> = mutableStateOf(null)

	private val themeFlow = MutableStateFlow<Boolean?>(null)
	val darkThemeFlow = themeFlow.asStateFlow()

	fun setAppTheme(darkTheme: Boolean?) {
		this.darkTheme.value = darkTheme
		themeFlow.value = darkTheme
	}

	fun getAppTheme(): Boolean? = darkTheme.value

	@Composable
	fun isDarkTheme(): Boolean = darkTheme.value ?: isSystemInDarkTheme()
}

fun ThemeChoice.toBoolean(): Boolean? = when (this) {
	ThemeChoice.Light -> false
	ThemeChoice.Dark -> true
	ThemeChoice.System -> null
}

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
fun themeChoice(): ThemeChoice = when (SelectedTheme.getAppTheme()) {
	null -> ThemeChoice.System
	false -> ThemeChoice.Light
	true -> ThemeChoice.Dark
}