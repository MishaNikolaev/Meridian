package com.nmichail.meridian.component.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SelectedTheme {

	private val themeFlow = MutableStateFlow<Boolean?>(null)
	val darkThemeFlow = themeFlow.asStateFlow()

	fun setAppTheme(darkTheme: Boolean?) {
		themeFlow.value = darkTheme
	}

	fun getAppTheme(): Boolean? = themeFlow.value

	@Composable
	fun isDarkTheme(): Boolean {
		val selected by themeFlow.collectAsState()
		return selected ?: isSystemInDarkTheme()
	}
}