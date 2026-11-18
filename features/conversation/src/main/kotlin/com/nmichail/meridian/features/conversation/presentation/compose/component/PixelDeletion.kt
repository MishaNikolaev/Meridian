package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.layout.layout
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import kotlin.random.Random

internal class PixelDeletion(initial: ChatLine) {
	var line by mutableStateOf(initial)
	var pixels by mutableStateOf<List<Pixel>?>(null)
	var finished by mutableStateOf(false)
	var span = 0f
	val anim = Animatable(0f)
	var intact = initial
	var animate = !initial.deleted
	lateinit var layer: GraphicsLayer
}

@Composable
internal fun rememberPixelDeletion(line: ChatLine): PixelDeletion {
	val layer = rememberGraphicsLayer()
	val deletion = remember(line.id) { PixelDeletion(line) }
	deletion.layer = layer

	if (!line.deleted) {
		deletion.intact = line
	}

	deletion.line = if (line.deleted && !deletion.finished) deletion.intact else line

	LaunchedEffect(line.deleted) {
		if (!line.deleted || !deletion.animate || deletion.finished) {
			return@LaunchedEffect
		}

		withFrameNanos { }
		val bitmap = deletion.layer.toImageBitmap()
		deletion.span = bitmap.height.toFloat()
		deletion.pixels = bitmap.pixels()
		deletion.anim.snapTo(0f)
		deletion.anim.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 420, easing = EaseIn))
		deletion.finished = true
	}

	return deletion
}

@Composable
internal fun DeletingBubble(
	deletion: PixelDeletion,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	Box(
		modifier = Modifier
			.layout { measurable, constraints ->
				val placeable = measurable.measure(constraints)
				val falling = deletion.pixels != null && !deletion.finished
				val progress = if (falling) deletion.anim.value else 0f
				val height = (placeable.height * (1f - progress)).toInt()
				layout(width = placeable.width, height = height) {
					placeable.place(x = 0, y = height - placeable.height)
				}
			}
			.graphicsLayer { clip = false }
			.drawWithContent {
				val pieces = deletion.pixels
				val progress = deletion.anim.value

				if (pieces == null || progress <= 0f || deletion.finished) {
					deletion.layer.record {
						this@drawWithContent.drawContent()
					}
					drawLayer(deletion.layer)
				} else {
					drawPixels(pieces, progress, size.height - deletion.span)
				}
			}
			.then(modifier),
	) {
		content()
	}
}

@Suppress("MagicNumber")
private fun ImageBitmap.pixels(): List<Pixel> {
	val map = toPixelMap()
	val step = (map.width / 22).coerceIn(10, 16)
	val dust = step * 0.8f
	val random = Random.Default
	val pieces = ArrayList<Pixel>()
	var y = 0

	while (y < map.height) {
		var x = 0

		while (x < map.width) {
			val color = map[(x + step / 2).coerceAtMost(map.width - 1), (y + step / 2).coerceAtMost(map.height - 1)]

			if (color.alpha > 0.08f) {
				pieces += Pixel(
					x = x.toFloat(),
					y = y.toFloat(),
					size = dust,
					color = color,
					drift = random.nextFloat() * 2f - 1f,
					delay = random.nextFloat() * 0.18f,
				)
			}

			x += step
		}

		y += step
	}

	return pieces
}

@Suppress("MagicNumber")
private fun DrawScope.drawPixels(pixels: List<Pixel>, progress: Float, shift: Float) {
	pixels.forEach { pixel ->
		val span = (1f - pixel.delay).coerceAtLeast(0.01f)
		val local = ((progress - pixel.delay) / span).coerceIn(0f, 1f)
		val fall = local * local

		drawRect(
			color = pixel.color.copy(alpha = pixel.color.alpha * (1f - fall)),
			topLeft = Offset(
				x = pixel.x + pixel.drift * 10f * local,
				y = pixel.y + shift + 22f * fall,
			),
			size = Size(width = pixel.size, height = pixel.size),
		)
	}
}
