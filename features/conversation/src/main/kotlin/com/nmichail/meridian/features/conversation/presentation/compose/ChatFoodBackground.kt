package com.nmichail.meridian.features.conversation.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R

@Composable
internal fun Modifier.chatFoodBackground(): Modifier {
	val base = palette.chatBackground

	val ink = ColorFilter.tint(palette.textSecondary.copy(alpha = 0.22f))

	val painter = painterResource(R.drawable.bg_i_like_food)

	return background(base).drawBehind {
		val tile = 130.dp.toPx()

		val columns = (size.width / tile).toInt() + 1

		val rows = (size.height / tile).toInt() + 1

		val count = columns * rows

		for (index in 0 until count) {
			translate(left = (index % columns) * tile, top = (index / columns) * tile) {
				with(painter) {
					draw(size = Size(width = tile, height = tile), colorFilter = ink)
				}
			}
		}
	}
}