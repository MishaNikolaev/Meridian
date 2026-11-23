package com.nmichail.meridian.features.conversation.presentation.compose.voice

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R
import kotlinx.coroutines.delay

private const val BARS = 12
private const val BAR_LIMIT = 48
private const val STEP = 90L
private const val QUIET = 0.12f
private const val BAR_FLOOR = 4f
private const val BAR_SPAN = 20f

@Composable
@Suppress("MagicNumber")
internal fun RecordBar(level: Float, modifier: Modifier = Modifier) {
	var seconds by remember { mutableIntStateOf(0) }

	LaunchedEffect(Unit) {
		while (true) {
			delay(timeMillis = 1000)
			seconds += 1
		}
	}

	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(10.dp),
	) {
		Image(
			painter = painterResource(R.drawable.ic_rec_dot),
			contentDescription = null,
			modifier = Modifier.size(10.dp),
		)

		Text(
			text = voiceClock(seconds),
			style = MeridianType.bodyName,
			color = palette.textPrimary,
		)

		VolumeLine(
			level = level,
			modifier = Modifier.weight(1f),
		)
		Row(
			horizontalArrangement = Arrangement.spacedBy(2.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Image(
				painter = painterResource(R.drawable.ic_chevron_down),
				contentDescription = null,
				modifier = Modifier
					.size(16.dp)
					.graphicsLayer { rotationZ = -90f },
			)

			Text(
				text = stringResource(R.string.conversation_voice_cancel),
				style = MeridianType.captionDefault,
				color = palette.textSecondary,
			)
		}
	}
}

@Composable
@Suppress("MagicNumber")
private fun VolumeLine(level: Float, modifier: Modifier = Modifier) {
	BoxWithConstraints(modifier = modifier) {
		val bars = (maxWidth / (3.dp + 2.dp)).toInt().coerceIn(BARS, BAR_LIMIT)
		val wave = rememberWave(level, bars)

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(2.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			wave.forEach { sample ->
				Box(
					modifier = Modifier
						.width(3.dp)
						.height((BAR_FLOOR + BAR_SPAN * sample).dp)
						.clip(RoundedCornerShape(1.5.dp))
						.background(palette.accentPrimary),
				)
			}
		}
	}
}

@Composable
private fun rememberWave(level: Float, bars: Int): List<Float> {
	var wave by remember(bars) { mutableStateOf(List(bars) { QUIET }) }
	val heard = rememberUpdatedState(level)

	LaunchedEffect(bars) {
		while (true) {
			delay(timeMillis = STEP)
			val next = wave.drop(1) + heard.value.coerceIn(0f, 1f)
			wave = if (next.size == bars) next else List(bars) { index -> next.getOrElse(index) { QUIET } }
		}
	}

	return wave
}

@Composable
@Suppress("MagicNumber")
internal fun VoiceButton(
	voice: Boolean,
	recording: Boolean,
	onRecording: (Boolean) -> Unit,
	onLevel: (Float) -> Unit,
	onVoice: (ByteArray, Int) -> Unit,
	onSend: () -> Unit,
) {
	val context = LocalContext.current
	val capture = remember { VoiceCapture(context) }
	val ask = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

	DisposableEffect(capture) {
		onDispose { capture.cancel() }
	}

	LaunchedEffect(recording) {
		while (recording) {
			onLevel(capture.level() / 32767f)
			delay(timeMillis = 80)
		}
	}

	val side = 34.dp
	val mic = voice || recording
	val icon = if (mic) R.drawable.ic_mic else R.drawable.ic_send
	val iconWidth = if (mic) 18.dp else 16.dp
	val iconHeight = if (mic) 18.dp else 14.dp

	Box(
		modifier = Modifier
			.size(side)
			.shadow(
				elevation = if (recording) 8.dp else 0.dp,
				shape = CircleShape,
				ambientColor = palette.recordShadow,
				spotColor = palette.recordShadow,
			)
			.clip(CircleShape)
			.background(palette.accentButton)
			.then(
				if (voice) {
					Modifier.pointerInput(Unit) {
						awaitEachGesture {
							recordHold(
								context = context,
								capture = capture,
								ask = { ask.launch(Manifest.permission.RECORD_AUDIO) },
								onRecording = onRecording,
								onLevel = onLevel,
								onVoice = onVoice,
							)
						}
					}
				} else {
					Modifier.pointerInput(Unit) {
						awaitEachGesture { tapSend(onSend) }
					}
				},
			),
		contentAlignment = Alignment.Center,
	) {
		Image(
			painter = painterResource(icon),
			contentDescription = stringResource(if (voice) R.string.conversation_voice else R.string.conversation_send),
			modifier = Modifier.size(width = iconWidth, height = iconHeight),
		)
	}
}

@Suppress("MagicNumber")
private fun voiceClock(seconds: Int): String {
	val minutes = seconds / 60
	val rest = seconds % 60
	return "$minutes:${rest.toString().padStart(length = 2, padChar = '0')}"
}