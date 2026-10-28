package com.nmichail.meridian.component.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nmichail.meridian.component.ui.theme.MeridianTheme

@Composable
fun Screen(
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	MeridianTheme {
		Surface(
			modifier = modifier
				.fillMaxSize()
				.safeDrawingPadding(),
			color = MaterialTheme.colorScheme.background,
		) {
			Box(modifier = Modifier.fillMaxSize()) {
				content()
			}
		}
	}
}
