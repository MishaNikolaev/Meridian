package com.nmichail.meridian.features.conversation.presentation.compose.component

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.nmichail.meridian.component.photopicker.photoLoader
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.conversation.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun MarkedPhoto(
	uri: Uri,
	name: String,
	marked: Boolean,
	onClick: () -> Unit,
	video: Boolean = false,
) {
	Box(
		modifier = Modifier
			.aspectRatio(1f)
			.clip(RoundedCornerShape(8.dp))
			.clickable(onClick = onClick),
	) {
		if (video) {
			VideoFrame(uri = uri, name = name)
		} else {
			AsyncImage(
				model = uri,
				contentDescription = name,
				imageLoader = photoLoader(),
				contentScale = ContentScale.Crop,
				modifier = Modifier.fillMaxSize(),
			)
		}
		if (marked) {
			Box(
				modifier = Modifier
					.align(Alignment.TopEnd)
					.padding(4.dp)
					.size(22.dp)
					.clip(CircleShape)
					.background(palette.accentPrimary),
				contentAlignment = Alignment.Center,
			) {
				Text(text = "✓", color = Color.White, style = MeridianType.chatMeta)
			}
		}
	}
}

@Composable
private fun VideoFrame(uri: Uri, name: String) {
	val context = LocalContext.current
	val frame by produceState<ImageBitmap?>(initialValue = null, uri) {
		value = withContext(Dispatchers.IO) { context.frame(uri) }
	}
	val shown = frame

	Box(modifier = Modifier.fillMaxSize()) {
		if (shown != null) {
			Image(
				bitmap = shown,
				contentDescription = name,
				contentScale = ContentScale.Crop,
				modifier = Modifier.fillMaxSize(),
			)
		}
		Box(
			modifier = Modifier
				.align(Alignment.Center)
				.size(22.dp)
				.clip(CircleShape)
				.background(Color.Black.copy(alpha = 0.45f)),
			contentAlignment = Alignment.Center,
		) {
			Text(text = "▶", color = Color.White, style = MeridianType.chatMeta)
		}
	}
}

@Suppress("MagicNumber")
private fun Context.frame(uri: Uri): ImageBitmap? = runCatching {
	val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
		contentResolver.loadThumbnail(uri, Size(256, 256), null)
	} else {
		@Suppress("DEPRECATION")
		MediaStore.Video.Thumbnails.getThumbnail(
			contentResolver,
			ContentUris.parseId(uri),
			MediaStore.Video.Thumbnails.MINI_KIND,
			null,
		)
	}

	bitmap?.asImageBitmap()
}.getOrNull()

@Composable
internal fun PhotoSend(count: Int, onSend: () -> Unit, modifier: Modifier = Modifier) {
	Row(
		modifier = modifier.padding(end = 16.dp, bottom = 12.dp),
		horizontalArrangement = Arrangement.End,
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			text = count.toString(),
			style = MeridianType.bodyDefault,
			color = palette.textSecondary,
			modifier = Modifier.padding(end = 12.dp),
		)
		Box(
			modifier = Modifier
				.size(44.dp)
				.clip(CircleShape)
				.background(palette.accentPrimary)
				.clickable(onClick = onSend),
			contentAlignment = Alignment.Center,
		) {
			Text(text = "✓", color = Color.White, style = MeridianType.titleCard)
		}
	}
}

@Composable
internal fun PhotoPreviews(previews: List<ByteArray>, onClear: () -> Unit) {
	val context = LocalContext.current

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.horizontalScroll(rememberScrollState())
			.background(palette.bar)
			.padding(start = 14.dp, end = 14.dp, top = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		previews.forEach { bytes ->
			AsyncImage(
				model = ImageRequest.Builder(context).data(bytes).build(),
				contentDescription = null,
				imageLoader = photoLoader(),
				contentScale = ContentScale.Crop,
				modifier = Modifier
					.size(64.dp)
					.clip(RoundedCornerShape(8.dp)),
			)
		}
		Text(
			text = stringResource(R.string.conversation_file_remove),
			style = MeridianType.chatMeta,
			color = palette.accentPrimary,
			modifier = Modifier.clickable(onClick = onClear),
		)
	}
}
