package com.nmichail.meridian.component.photopicker.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.nmichail.meridian.component.photopicker.R
import com.nmichail.meridian.component.photopicker.photoLoader
import com.nmichail.meridian.component.photopicker.photoUrl
import com.nmichail.meridian.component.ui.initials.initials
import com.nmichail.meridian.component.ui.initials.markColor
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette

@Composable
fun PhotoMark(
	name: String,
	photoId: String,
	enabled: Boolean,
	onOpen: () -> Unit,
) {
	val context = LocalContext.current

	val openPicker = {
		if (enabled) {
			onOpen()
		}
	}

	Box(modifier = Modifier.size(76.dp)) {
		Box(
			modifier = Modifier
				.size(76.dp)
				.graphicsLayer {
					clip = true
					shape = CircleShape
					compositingStrategy = CompositingStrategy.Offscreen
				}
				.background(markColor(name))
				.clickable(enabled = enabled, onClick = openPicker),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = initials(name),
				style = MeridianType.titleCard.copy(fontSize = 27.sp, fontWeight = FontWeight.SemiBold),
				color = palette.textOnAccent,
			)

			val url = photoUrl(photoId)

			if (url.isNotEmpty()) {
				AsyncImage(
					model = ImageRequest.Builder(context)
						.data(url)
						.crossfade(enable = true)
						.build(),
					contentDescription = stringResource(R.string.photo_profile),
					imageLoader = photoLoader(),
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.matchParentSize()
						.graphicsLayer(
							scaleX = 1.02f,
							scaleY = 1.02f,
						),
				)
			}
		}

		CameraBadge(
			enabled = enabled,
			onClick = openPicker,
			modifier = Modifier.align(Alignment.BottomEnd),
		)
	}
}

@Composable
private fun CameraBadge(
	enabled: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.size(26.dp)
			.border(width = 2.5.dp, color = palette.surface, shape = CircleShape)
			.padding(2.5.dp)
			.clip(CircleShape)
			.background(palette.field)
			.clickable(enabled = enabled, onClick = onClick),
		contentAlignment = Alignment.Center,
	) {
		Image(
			painter = painterResource(R.drawable.ic_photo_camera),
			contentDescription = stringResource(R.string.photo_profile),
			colorFilter = ColorFilter.tint(palette.textSecondary),
			modifier = Modifier.size(13.dp),
		)
	}
}