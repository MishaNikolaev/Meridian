package com.nmichail.meridian.features.conversation.presentation.compose.voice

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.playback.MediaPlayback

@Composable
@Suppress("MagicNumber")
internal fun VoiceNote(name: String, fileId: String, onPlay: () -> Unit) {
	val context = LocalContext.current
	val phase by MediaPlayback.phase.collectAsStateWithLifecycle()
	val active = phase.id == fileId && fileId.isNotEmpty()
	val playing = active && phase.playing
	val bars = listOf(8, 14, 10, 18, 12, 20, 9, 16, 22, 11, 18, 8, 15)

	Column(
		verticalArrangement = Arrangement.spacedBy(6.dp),
		modifier = Modifier.padding(bottom = 14.dp, end = 52.dp),
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(8.dp),
		) {
			PlayMark(playing = playing) {
				when {
					playing -> MediaPlayback.pause(context)
					active -> MediaPlayback.resume(context)
					else -> onPlay()
				}
			}
			Row(
				horizontalArrangement = Arrangement.spacedBy(2.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				repeat(times = 6) {
					Box(
						modifier = Modifier
							.size(3.dp)
							.clip(CircleShape)
							.background(Color(color = 0xFF3B82F6)),
					)
				}
				bars.forEach { height ->
					Box(
						modifier = Modifier
							.size(width = 3.dp, height = height.dp)
							.clip(RoundedCornerShape(1.dp))
							.background(Color(color = 0xFF3B82F6)),
					)
				}
			}
		}
		Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
			Text(text = voiceLength(name), style = MeridianType.captionSmall, color = palette.textSecondary)
			Box(
				modifier = Modifier
					.size(6.dp)
					.clip(CircleShape)
					.background(Color(color = 0xFF2563EB)),
			)
		}
	}
}

@Composable
@Suppress("MagicNumber")
private fun PlayMark(playing: Boolean, onPlay: () -> Unit) {
	Box(
		modifier = Modifier
			.size(42.dp)
			.clip(CircleShape)
			.pointerInput(onPlay) {
				awaitEachGesture {
					val down = awaitFirstDown(pass = PointerEventPass.Initial)
					val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
					if (up != null) {
						down.consume()
						up.consume()
						onPlay()
					}
				}
			},
		contentAlignment = Alignment.Center,
	) {
		Canvas(modifier = Modifier.size(42.dp)) {
			drawCircle(color = Color(color = 0xFF2563EB))
			if (playing) {
				val top = size.height * 0.3f
				val height = size.height * 0.4f
				val thick = size.width * 0.1f
				drawRect(Color.White, Offset(size.width * 0.36f, top), Size(thick, height))
				drawRect(Color.White, Offset(size.width * 0.56f, top), Size(thick, height))
			} else {
				val path = Path().apply {
					moveTo(size.width * 0.4f, size.height * 0.3f)
					lineTo(size.width * 0.72f, size.height * 0.5f)
					lineTo(size.width * 0.4f, size.height * 0.7f)
					close()
				}
				drawPath(path, Color.White)
			}
		}
	}
}

private fun voiceLength(name: String): String {
	val seconds = name.substringAfter("voice-", "").substringBefore('.').toIntOrNull() ?: 0
	return clock(seconds = seconds, minute = 60)
}

private fun clock(seconds: Int, minute: Int): String {
	val minutes = seconds / minute
	val rest = seconds % minute
	return "$minutes:${rest.toString().padStart(length = 2, padChar = '0')}"
}