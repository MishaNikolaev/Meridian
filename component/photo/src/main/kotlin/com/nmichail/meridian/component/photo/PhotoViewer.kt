package com.nmichail.meridian.component.photo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import coil3.request.ImageRequest
import com.nmichail.meridian.component.photopicker.photoLoader
import com.nmichail.meridian.component.ui.R as UiR

@Composable
fun PhotoViewer(bytes: ByteArray, onClose: () -> Unit) {
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
			AsyncImage(
				model = ImageRequest.Builder(context).data(bytes).build(),
				contentDescription = null,
				imageLoader = photoLoader(),
				contentScale = ContentScale.Fit,
				modifier = Modifier.fillMaxSize(),
			)
			Row(
				modifier = Modifier
					.align(Alignment.TopStart)
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
	}
}
