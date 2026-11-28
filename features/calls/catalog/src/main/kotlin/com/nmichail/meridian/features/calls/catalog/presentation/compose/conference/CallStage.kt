package com.nmichail.meridian.features.calls.catalog.presentation.compose.conference

import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.calls.catalog.R
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.LocalMeetingPreview
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.LocalCallCapture
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.WithCallCamera
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.callTime
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.rememberStageSeconds

internal enum class LiveCall {
	Solo,
	Meeting,
}

@Composable
fun ConferenceRoom(title: String, onClose: () -> Unit) {
	CallStage(kind = LiveCall.Meeting, title = title, onClose = onClose, selfName = "", profilePhoto = "", cameraEnabled = null, onToggleCamera = {})
}

@Composable
internal fun CallStage(
	kind: LiveCall,
	title: String,
	onClose: () -> Unit,
	selfName: String,
	profilePhoto: String,
	cameraEnabled: Boolean?,
	onToggleCamera: () -> Unit,
) {
	val shared = cameraEnabled != null || LocalCallCapture.current != null
	if (!shared) {
		WithCallCamera {
			Stage(
				kind = kind,
				title = title,
				onClose = onClose,
				selfName = selfName,
				profilePhoto = profilePhoto,
				cameraEnabled = cameraEnabled,
				onToggleCamera = onToggleCamera,
			)
		}
	} else {
		Stage(
			kind = kind,
			title = title,
			onClose = onClose,
			selfName = selfName,
			profilePhoto = profilePhoto,
			cameraEnabled = cameraEnabled,
			onToggleCamera = onToggleCamera,
		)
	}
}

@Composable
private fun Stage(
	kind: LiveCall,
	title: String,
	onClose: () -> Unit,
	selfName: String,
	profilePhoto: String,
	cameraEnabled: Boolean?,
	onToggleCamera: () -> Unit,
) {
	val context = LocalContext.current
	val audio = remember(context) { context.getSystemService(AudioManager::class.java) }
	val capture = LocalCallCapture.current ?: LocalMeetingPreview.current?.capture
	var muted by remember { mutableStateOf(false) }
	var localCamera by remember { mutableStateOf(true) }
	val camera = cameraEnabled ?: localCamera
	var speaker by remember { mutableStateOf(true) }
	val speaking = rememberSpeaking(active = !muted)
	val meeting = title.ifBlank { stringResource(R.string.calls_live_title) }
	HoldSpeaker(audio)
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		when (kind) {
			LiveCall.Solo    -> SoloStage(
				modifier = Modifier.weight(1f),
				name = stringResource(R.string.calls_solo_name),
				speaking = speaking,
				muted = muted,
				camera = camera,
				selfName = selfName,
				profilePhoto = profilePhoto,
			)

			LiveCall.Meeting -> MeetingStage(
				modifier = Modifier.weight(1f),
				title = meeting,
				speaking = speaking,
				muted = muted,
				camera = camera,
				selfName = selfName,
				profilePhoto = profilePhoto,
			)
		}
		CallControls(
			muted = muted,
			camera = camera,
			speaker = speaker,
			onMute = { muted = !muted },
			onCamera = {
				if (cameraEnabled == null) {
					localCamera = !camera
					capture?.setCamera(localCamera)
				} else {
					onToggleCamera()
				}
			},
			onSpeaker = {
				val next = !speaker
				speaker = next
				routeSpeaker(audio, next)
			},
			onClose = onClose,
		)
	}
}

@Composable
private fun HoldSpeaker(audio: AudioManager?) {
	DisposableEffect(audio) {
		routeSpeaker(audio, true)
		onDispose {
			audio?.mode = AudioManager.MODE_NORMAL
			audio?.isSpeakerphoneOn = false
		}
	}
}

@Composable
private fun SoloStage(
	modifier: Modifier,
	name: String,
	speaking: Boolean,
	muted: Boolean,
	camera: Boolean,
	selfName: String,
	profilePhoto: String,
) {
	val seconds = rememberStageSeconds()
	Column(
		modifier = modifier.padding(horizontal = 16.dp),
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(10.dp),
		) {
			Image(
				painter = painterResource(R.drawable.ic_call_down),
				contentDescription = null,
				modifier = Modifier.size(22.dp),
			)
			Column {
				Text(
					text = name,
					style = MeridianType.bodyDefault,
					color = palette.textPrimary,
				)

				Text(
					text = callTime(seconds),
					style = MeridianType.captionSmall,
					color = palette.textSecondary,
				)
			}
		}
		Box(
			modifier = Modifier
				.padding(top = 12.dp)
				.fillMaxWidth()
				.weight(1f),
		) {
			FaceTile(
				face = FacePreview(name, R.drawable.call_preview_solo),
				modifier = Modifier.fillMaxSize(),
			)

			SelfPreview(
				speaking = speaking && !muted,
				camera = camera,
				name = selfName.ifBlank { stringResource(R.string.calls_you) },
				photoId = profilePhoto,
				modifier = Modifier
					.align(Alignment.BottomEnd)
					.padding(12.dp)
					.size(width = 92.dp, height = 124.dp),
			)
		}
	}
}

@Composable
private fun MeetingStage(
	modifier: Modifier,
	title: String,
	speaking: Boolean,
	muted: Boolean,
	camera: Boolean,
	selfName: String,
	profilePhoto: String,
) {
	val seconds = rememberStageSeconds()
	Column(modifier = modifier.padding(horizontal = 12.dp)) {

		Text(
			text = title,
			style = MeridianType.bodyName,
			color = palette.textPrimary
		)

		Text(
			text = stringResource(R.string.calls_live_meta, callTime(seconds)),
			style = MeridianType.captionSmall,
			color = palette.textSecondary,
		)

		FaceTile(
			face = FacePreview(
				name = selfName.ifBlank { stringResource(R.string.calls_you) },
				photoId = profilePhoto,
				muted = muted,
				speaking = speaking && !muted,
				live = camera,
			),
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.padding(top = 8.dp),
		)
	}
}

@Composable
private fun CallControls(
	muted: Boolean,
	camera: Boolean,
	speaker: Boolean,
	onMute: () -> Unit,
	onCamera: () -> Unit,
	onSpeaker: () -> Unit,
	onClose: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.navigationBarsPadding()
			.padding(top = 20.dp, bottom = 36.dp),
		horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
	) {
		Control(
			icon = if (muted) {
				R.drawable.ic_call_mic_off
			} else {
				R.drawable.ic_call_mic_light
			},
			color = palette.textSecondary,
			onClick = onMute,
		)
		Control(
			icon = if (camera) {
				R.drawable.ic_call_video
			} else {
				R.drawable.ic_call_video_off
			},
			color = palette.textSecondary,
			onClick = onCamera,
		)
		Control(
			icon = R.drawable.ic_call_volume,
			color = palette.textSecondary,
			onClick = onSpeaker,
		)
		Control(
			icon = R.drawable.ic_call_more,
			color = palette.textSecondary,
		)
		Control(
			icon = R.drawable.ic_call_end,
			color = Color(color = 0xFFEF4444),
			onClick = onClose,
		)
	}
}

@Composable
private fun Control(icon: Int, color: Color, onClick: () -> Unit = {}) {
	Box(
		modifier = Modifier
			.size(54.dp)
			.clip(CircleShape)
			.background(color)
			.clickable(onClick = onClick),
		contentAlignment = Alignment.Center,
	) {
		Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(24.dp))
	}
}

private fun routeSpeaker(audio: AudioManager?, on: Boolean) {
	val manager = audio ?: return
	manager.mode = AudioManager.MODE_IN_COMMUNICATION
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
		val wanted = if (on) {
			AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
		} else {
			AudioDeviceInfo.TYPE_BUILTIN_EARPIECE
		}
		manager.availableCommunicationDevices
			.firstOrNull { device -> device.type == wanted }
			?.let(manager::setCommunicationDevice)
	}
	manager.isSpeakerphoneOn = on
}