package com.nmichail.meridian.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.nmichail.meridian.component.ui.theme.MeridianTheme as ComponentTheme

@Composable
fun MeridianTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit,
) {
	ComponentTheme(darkTheme = darkTheme, content = content)
}
