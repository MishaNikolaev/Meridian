package com.nmichail.meridian.component.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nmichail.meridian.component.photopicker.photoLoader
import com.nmichail.meridian.component.photopicker.photoRequest
import com.nmichail.meridian.component.ui.initials.initials
import com.nmichail.meridian.component.ui.initials.markColor
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette

@Composable
fun PhotoFill(
	name: String,
	modifier: Modifier,
	photoId: String,
) {
	val pixels = with(LocalDensity.current) { 320.dp.roundToPx() }
	val hasPhoto = photoId.isNotBlank()

	Box(
		modifier = modifier
			.fillMaxSize()
			.then(if (!hasPhoto) Modifier.background(markColor(name)) else Modifier),
		contentAlignment = Alignment.Center,
	) {
		if (!hasPhoto) {
			Text(
				text = initials(name),
				style = MeridianType.titleCard,
				color = palette.textOnAccent,
			)
		} else {
			AsyncImage(
				model = photoRequest(LocalContext.current, photoId, pixels),
				contentDescription = name,
				imageLoader = photoLoader(),
				contentScale = ContentScale.Crop,
				modifier = Modifier.fillMaxSize(),
			)
		}
	}
}