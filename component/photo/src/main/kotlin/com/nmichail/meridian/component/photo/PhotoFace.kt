package com.nmichail.meridian.component.photo

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import com.nmichail.meridian.component.photopicker.PhotoLocalDataSource
import com.nmichail.meridian.component.photopicker.photoLoader
import com.nmichail.meridian.component.photopicker.photoRequest
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
	val stored = if (chatId.isNotEmpty()) {
		PhotoLocalDataSource.storedChatPhoto(chatId)
	} else {
		PhotoLocalDataSource.storedPersonPhoto(userId)
	}
	val photo = stored.ifEmpty { photoId }
	val pixels = with(LocalDensity.current) { size.roundToPx() }
	val shape = RoundedCornerShape(corner)

	val url = photoUrl(photo)

	Box(
		modifier = modifier
			.size(size)
			.graphicsLayer {
				clip = true

				this.shape = shape

				compositingStrategy = CompositingStrategy.Offscreen
			}
			.then(if (url.isEmpty()) Modifier.background(markColor(name)) else Modifier),
		contentAlignment = Alignment.Center,
	) {
		if (url.isEmpty()) {
			Text(
				text = initials(name),
				style = style,
				color = palette.textOnAccent,
			)
		} else {
			AsyncImage(
				model = photoRequest(LocalContext.current, photo, pixels),
				contentDescription = null,
				imageLoader = photoLoader(),
				contentScale = ContentScale.Crop,
				modifier = Modifier.matchParentSize(),
			)
		}
	}
}
