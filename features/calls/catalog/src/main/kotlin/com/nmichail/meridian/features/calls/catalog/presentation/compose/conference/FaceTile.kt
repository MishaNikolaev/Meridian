package com.nmichail.meridian.features.calls.catalog.presentation.compose.conference

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.photo.PhotoFill
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.calls.catalog.R
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.CallVideo
import com.nmichail.meridian.features.calls.catalog.presentation.compose.media.LocalCallCapture

private val speakingColor = Color(color = 0xFF22C55E)

@Composable
internal fun FaceTile(face: FacePreview, modifier: Modifier = Modifier) {
	Box(
		modifier = modifier
			.clip(RoundedCornerShape(18.dp))
			.then(
				if (face.speaking) {
					Modifier.border(width = 3.dp, color = speakingColor, shape = RoundedCornerShape(18.dp))
				} else {
					Modifier
				},
			),
	) {
		if (face.live) {
			CallVideo(modifier = Modifier.fillMaxSize())
		} else if (face.photo != 0) {
			Image(
				painter = painterResource(face.photo),
				contentDescription = face.name,
				contentScale = ContentScale.Crop,
				modifier = Modifier.fillMaxSize(),
			)
		} else {
			PhotoFill(
				name = face.name,
				modifier = Modifier,
				photoId = face.photoId,
			)
		}
		Row(
			modifier = Modifier
				.align(Alignment.BottomStart)
				.padding(8.dp)
				.clip(RoundedCornerShape(10.dp))
				.background(palette.captionScrim)
				.padding(horizontal = 8.dp, vertical = 3.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(4.dp),
		) {
			Image(
				painter = painterResource(if (face.muted) R.drawable.ic_call_mic_off else R.drawable.ic_call_mic_light),
				contentDescription = null,
				modifier = Modifier.size(12.dp),
			)
			Text(text = face.name, style = MeridianType.captionSmall, color = palette.textOnAccent, maxLines = 1)
		}
	}
}

@Composable
internal fun SelfPreview(
	speaking: Boolean,
	camera: Boolean,
	name: String,
	photoId: String,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.clip(RoundedCornerShape(16.dp))
			.then(
				if (speaking) {
					Modifier.border(width = 3.dp, color = speakingColor, shape = RoundedCornerShape(16.dp))
				} else {
					Modifier
				},
			)
			.background(palette.surface),
	) {
		if (LocalCallCapture.current == null || !camera) {
			PhotoFill(name = name, modifier = Modifier, photoId = photoId)
		} else {
			CallVideo()
		}
		Text(
			text = stringResource(R.string.calls_you),
			style = MeridianType.captionSmall,
			color = palette.textPrimary,
			modifier = Modifier
				.align(Alignment.BottomStart)
				.padding(8.dp)
				.clip(RoundedCornerShape(12.dp))
				.background(palette.page)
				.padding(horizontal = 9.dp, vertical = 4.dp),
		)
	}
}