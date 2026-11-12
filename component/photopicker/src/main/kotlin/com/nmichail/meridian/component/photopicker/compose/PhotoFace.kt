package com.nmichail.meridian.component.photopicker.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.nmichail.meridian.component.photopicker.PhotoLocalDataSource
import com.nmichail.meridian.component.photopicker.photoLoader
import com.nmichail.meridian.component.photopicker.photoUrl
import com.nmichail.meridian.component.ui.initials.initials
import com.nmichail.meridian.component.ui.initials.markColor
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette

@Composable
fun PhotoFace(
	name: String,
	size: Dp,
	modifier: Modifier = Modifier,
	userId: String = "",
	chatId: String = "",
	photoId: String = "",
	corner: Dp = size / 2,
	style: TextStyle = MeridianType.bodyName,
) {
	val stored = storedPhoto(userId = userId, chatId = chatId)
	val photo = photoId.ifEmpty { stored }
	val shape = RoundedCornerShape(corner)

	Box(
		modifier = modifier
			.size(size)
			.graphicsLayer {
				clip = true

				this.shape = shape

				compositingStrategy = CompositingStrategy.Offscreen
			}
			.background(markColor(name)),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = initials(name),
			style = style,
			color = palette.textOnAccent,
		)

		val url = photoUrl(photo)

		if (url.isNotEmpty()) {
			AsyncImage(
				model = ImageRequest.Builder(LocalContext.current)
					.data(url)
					.crossfade(enable = true)
					.build(),
				contentDescription = null,
				imageLoader = photoLoader(),
				contentScale = ContentScale.Crop,
				modifier = Modifier.matchParentSize(),
			)
		}
	}
}

@Composable
private fun storedPhoto(userId: String, chatId: String): String {
	if (chatId.isNotEmpty()) {
		return PhotoLocalDataSource.chats[chatId].orEmpty()
	}

	return PhotoLocalDataSource.ids[userId].orEmpty()
}