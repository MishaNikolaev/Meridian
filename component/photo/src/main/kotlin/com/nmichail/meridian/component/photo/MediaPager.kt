package com.nmichail.meridian.component.photo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.nmichail.meridian.component.photopicker.fullPhotoRequest
import com.nmichail.meridian.component.photopicker.photoLoader
import com.nmichail.meridian.component.ui.R as UiR

@Composable
fun MediaPager(
	items: List<MediaItem>,
	currentId: String,
	onClose: () -> Unit,
	onPlay: (MediaItem) -> Unit,
) {
	if (items.isEmpty()) {
		return
	}

	val start = items.indexOfFirst { item -> item.id == currentId }.coerceAtLeast(0)
	val pager = rememberPagerState(initialPage = start, pageCount = { items.size })
	val context = LocalContext.current

	Dialog(
		onDismissRequest = onClose,
		properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(Color.Black),
		) {
			HorizontalPager(
				state = pager,
				modifier = Modifier.fillMaxSize(),
			) { page ->
				val item = items[page]

				if (item.video) {
					VideoPage(item = item, onPlay = { onPlay(item) })
				} else {
					AsyncImage(
						model = fullPhotoRequest(context, item.id),
						contentDescription = item.name,
						imageLoader = photoLoader(),
						contentScale = ContentScale.Fit,
						modifier = Modifier.fillMaxSize(),
					)
				}
			}
			PagerBack(onClose = onClose, modifier = Modifier.align(Alignment.TopStart))
		}
	}
}

@Composable
private fun VideoPage(item: MediaItem, onPlay: () -> Unit) {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center,
	) {
		Image(
			painter = painterResource(R.drawable.ic_play),
			contentDescription = item.name,
			colorFilter = ColorFilter.tint(Color.White),
			modifier = Modifier
				.size(64.dp)
				.clickable(onClick = onPlay),
		)
		if (item.name.isNotEmpty()) {
			Text(
				text = item.name,
				color = Color.White,
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(24.dp),
			)
		}
	}
}

@Composable
private fun PagerBack(onClose: () -> Unit, modifier: Modifier = Modifier) {
	Row(
		modifier = modifier
			.statusBarsPadding()
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 14.dp),
	) {
		Image(
			painter = painterResource(UiR.drawable.ic_back),
			contentDescription = null,
			colorFilter = ColorFilter.tint(Color.White),
			modifier = Modifier
				.size(16.dp)
				.clickable(onClick = onClose),
		)
	}
}