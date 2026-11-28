package com.nmichail.meridian.features.calls.catalog.presentation.compose.ring

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.photo.PhotoFace
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.calls.catalog.R
import com.nmichail.meridian.features.calls.catalog.data.media.WebRtcCall
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.CallPicture
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.callTime
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.rememberLiveSeconds

private val decline = Color(color = 0xFFEF4444)
private val answer = Color(color = 0xFF22C55E)

@Composable
fun RingCall(
	name: String,
	userId: String,
	chatId: String,
	phase: RingPhase,
	video: Boolean,
	line: WebRtcCall?,
	onAccept: () -> Unit,
	onDecline: () -> Unit,
	onHangup: () -> Unit,
) {
	val current = line?.takeIf { video && phase != RingPhase.Incoming }
	val camera = current != null
	val remote = current?.mediaStatus?.collectAsState()?.value?.remoteVideo == true
	val ink = if (camera) Color.White else palette.textPrimary
	val note = if (camera) Color.White.copy(alpha = 0.72f) else palette.textSecondary
	val elapsed = rememberLiveSeconds(phase)
	Box(modifier = Modifier.fillMaxSize().background(if (camera) Color.Black else palette.page)) {
		if (current != null && remote) {
			CallPicture(line = current, remote = true, modifier = Modifier.fillMaxSize())
			CallPicture(
				line = current,
				remote = false,
				modifier = Modifier
					.align(Alignment.TopEnd)
					.padding(top = 48.dp, end = 16.dp)
					.size(width = 112.dp, height = 160.dp),
			)
		} else if (current != null) {
			CallPicture(line = current, remote = false, modifier = Modifier.fillMaxSize())
		}
		Column(
			modifier = Modifier
				.fillMaxSize()
				.statusBarsPadding()
				.navigationBarsPadding(),
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			Text(
				text = if (phase == RingPhase.Live) callTime(elapsed) else phase.label(),
				style = MeridianType.captionSmall,
				color = note,
				modifier = Modifier.padding(top = 28.dp),
			)
			val waiting = camera && !remote && phase == RingPhase.Live
			CallIdentity(
				name = name,
				userId = userId,
				chatId = chatId,
				showPhoto = !camera || waiting,
				pulse = phase != RingPhase.Live,
				color = ink,
				modifier = Modifier.weight(1f),
			)
			RingActions(
				phase = phase,
				line = line,
				onAccept = onAccept,
				onDecline = onDecline,
				onHangup = onHangup,
			)
		}
	}
}

@Composable
private fun CallIdentity(
	name: String,
	userId: String,
	chatId: String,
	showPhoto: Boolean,
	pulse: Boolean,
	color: Color,
	modifier: Modifier,
) {
	Box(
		modifier = modifier.fillMaxWidth(),
		contentAlignment = Alignment.Center,
	) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			if (showPhoto) {
				CallFace(name = name, userId = userId, chatId = chatId, pulse = pulse)
			}
			Text(
				text = name,
				style = MeridianType.titleCard,
				color = color,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.fillMaxWidth()
					.padding(start = 24.dp, top = 16.dp, end = 24.dp),
			)
		}
	}
}

@Composable
private fun CallFace(name: String, userId: String, chatId: String, pulse: Boolean) {
	val group = userId.isEmpty()
	Box(
		modifier = Modifier.size(232.dp),
		contentAlignment = Alignment.Center,
	) {
		if (pulse) {
			RingPulse()
		}
		PhotoFace(
			name = name,
			userId = if (group) "" else userId,
			chatId = if (group) chatId else "",
			size = 140.dp,
			style = MeridianType.titleCard,
		)
	}
}

@Composable
private fun RingPhase.label(): String = when (this) {
	RingPhase.Outgoing -> stringResource(R.string.calls_outgoing)
	RingPhase.Incoming -> stringResource(R.string.calls_incoming)
	RingPhase.Live -> stringResource(R.string.calls_talking)
}

@Composable
private fun RingActions(
	phase: RingPhase,
	line: WebRtcCall?,
	onAccept: () -> Unit,
	onDecline: () -> Unit,
	onHangup: () -> Unit,
) {
	Row(
		modifier = Modifier.padding(bottom = 36.dp),
		horizontalArrangement = Arrangement.spacedBy(28.dp),
	) {
		when (phase) {
			RingPhase.Incoming -> {
				RoundButton(R.drawable.ic_call_end, decline, onDecline)
				RoundButton(R.drawable.ic_call_answer, answer, onAccept)
			}
			RingPhase.Outgoing -> RoundButton(R.drawable.ic_call_end, decline, onDecline)
			RingPhase.Live -> {
				val media = line?.mediaStatus?.collectAsState()?.value
				val muted = media?.muted == true
				val speaker = media?.speaker == true
				RoundButton(
					icon = if (muted) R.drawable.ic_call_mic_off else R.drawable.ic_call_mic_light,
					color = palette.textPrimary,
					onClick = { line?.setMuted(!muted) },
				)
				RoundButton(
					icon = R.drawable.ic_call_volume,
					color = if (speaker) palette.textPrimary else palette.field,
					onClick = { line?.setSpeaker(!speaker) },
				)
				RoundButton(R.drawable.ic_call_end, decline, onHangup)
			}
		}
	}
}

@Composable
private fun RoundButton(icon: Int, color: Color, onClick: () -> Unit) {
	Box(
		modifier = Modifier
			.size(64.dp)
			.clip(CircleShape)
			.background(color)
			.clickable(onClick = onClick, enabled = true),
		contentAlignment = Alignment.Center,
	) {
		Image(
			painter = painterResource(icon),
			contentDescription = null,
			colorFilter = ColorFilter.tint(Color.White),
			modifier = Modifier.size(28.dp),
		)
	}
}
