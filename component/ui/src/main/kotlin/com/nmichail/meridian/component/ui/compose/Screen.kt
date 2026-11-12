package com.nmichail.meridian.component.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nmichail.meridian.component.ui.theme.MeridianTheme
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select

@Composable
fun Screen(
	modifier: Modifier = Modifier,
	canvas: Boolean = false,
	content: @Composable () -> Unit,
) {
	MeridianTheme {
		val background = select(
			active = canvas,
			on = palette.canvas,
			off = palette.page,
		)

		Surface(
			modifier = modifier
				.fillMaxSize()
				.safeDrawingPadding(),
			color = background,
		) {
			Box(modifier = Modifier.fillMaxSize()) {
				content()
			}
		}
	}
}