package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nmichail.meridian.component.photopicker.photoLoader
import com.nmichail.meridian.component.photopicker.photoRequest

@Composable
internal fun MessagePicture(fileId: String, onOpen: () -> Unit) {
	val context = LocalContext.current

	AsyncImage(
		model = photoRequest(context, fileId, pixels = 1440),
		contentDescription = null,
		imageLoader = photoLoader(),
		contentScale = ContentScale.Crop,
		modifier = Modifier
			.size(width = 280.dp, height = 372.dp)
			.clip(RoundedCornerShape(14.dp))
			.clickable(onClick = onOpen),
	)
}