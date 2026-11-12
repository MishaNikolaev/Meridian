package com.nmichail.meridian.component.photopicker.compose

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.attafitamim.krop.core.crop.AspectRatio
import com.attafitamim.krop.core.crop.CropResult
import com.attafitamim.krop.core.crop.crop
import com.attafitamim.krop.core.crop.cropperStyle
import com.attafitamim.krop.core.crop.rememberImageCropper
import com.nmichail.meridian.component.photopicker.R
import com.nmichail.meridian.component.photopicker.photoLoader
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PhotoPicker(
	open: Boolean,
	onDismiss: () -> Unit,
	onPhoto: (ByteArray, String) -> Unit,
	circle: Boolean = true,
) {
	val context = LocalContext.current

	val scope = rememberCoroutineScope()

	val cropper = rememberImageCropper()

	val chrome = cropChrome(circle)

	val style = remember(circle, chrome) {
		cropperStyle(
			backgroundColor = chrome.backdrop,
			rectColor = chrome.frame,
			overlay = chrome.shade,
			shapes = listOf(chrome.shape),
			aspects = listOf(AspectRatio(x = 1, y = 1)),
		)
	}

	val pending = cropper.cropState

	if (pending != null) {
		PhotoCrop(state = pending, style = style)
	}

	if (open && pending == null) {
		PhotoPickerDialog(
			onDismiss = onDismiss,
			onChoose = { uri ->
				scope.launch {
					val oriented = withContext(Dispatchers.IO) { context.orientedPhoto(uri) }

					if (oriented == null) {
						return@launch
					}

					val result = cropper.crop(
						maxResultSize = IntSize(width = 2048, height = 2048),
						bmp = oriented,
					)

					val bitmap = (result as? CropResult.Success)?.bitmap?.asAndroidBitmap()

					val saved = when (circle) {
						true -> bitmap?.toPng()
						false -> bitmap?.toJpeg()
					}

					if (saved != null) {
						onPhoto(saved.bytes, saved.type)

						onDismiss()
					}
				}
			},
		)
	}
}

@Composable
private fun PhotoPickerDialog(
	onDismiss: () -> Unit,
	onChoose: (Uri) -> Unit,
) {
	val context = LocalContext.current

	val scope = rememberCoroutineScope()

	var photos by remember { mutableStateOf<List<Uri>>(emptyList()) }

	var selected by remember { mutableStateOf<Uri?>(null) }

	val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		Manifest.permission.READ_MEDIA_IMAGES
	} else {
		Manifest.permission.READ_EXTERNAL_STORAGE
	}

	val ask = rememberLauncherForActivityResult(RequestPermission()) { granted ->
		if (granted) {
			scope.launch { photos = withContext(Dispatchers.IO) { recentPhotos(context) } }
		}
	}

	LaunchedEffect(Unit) {
		val granted = ContextCompat.checkSelfPermission(context, permission)

		if (granted == PackageManager.PERMISSION_GRANTED) {
			photos = withContext(Dispatchers.IO) { recentPhotos(context) }
		} else {
			ask.launch(permission)
		}
	}

	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(usePlatformDefaultWidth = false),
	) {
		PhotoSheet(
			photos = photos,
			selected = selected,
			onSelect = { uri -> selected = uri },
			onAsk = { ask.launch(permission) },
			onChoose = onChoose,
			onDismiss = onDismiss,
		)
	}
}

@Composable
private fun PhotoSheet(
	photos: List<Uri>,
	selected: Uri?,
	onSelect: (Uri) -> Unit,
	onAsk: () -> Unit,
	onChoose: (Uri) -> Unit,
	onDismiss: () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(start = 4.dp, end = 16.dp, top = 8.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Image(
				painter = painterResource(UiR.drawable.ic_back),
				contentDescription = stringResource(R.string.photo_cancel),
				colorFilter = ColorFilter.tint(palette.textPrimary),
				modifier = Modifier
					.size(48.dp)
					.clickable(onClick = onDismiss)
					.padding(16.dp),
			)

			Text(
				text = stringResource(R.string.photo_title),
				style = MeridianType.titleCard,
				color = palette.textPrimary,
			)
		}

		if (photos.isEmpty()) {
			EmptyPhotos(onAsk = onAsk, modifier = Modifier.weight(1f))
		} else {
			PhotoGrid(
				photos = photos,
				selected = selected,
				onSelect = onSelect,
				modifier = Modifier.weight(1f),
			)
		}

		DoneButton(selected = selected, onChoose = onChoose)
	}
}

@Composable
private fun EmptyPhotos(
	onAsk: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier
			.fillMaxWidth()
			.padding(24.dp),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(
			text = stringResource(R.string.photo_empty),
			style = MeridianType.bodyDefault,
			color = palette.textSecondary,
		)

		Text(
			text = stringResource(R.string.photo_allow),
			style = MeridianType.bodyDefault,
			color = palette.accentPrimary,
			modifier = Modifier
				.padding(top = 12.dp)
				.clickable(onClick = onAsk),
		)
	}
}

@Composable
private fun PhotoGrid(
	photos: List<Uri>,
	selected: Uri?,
	onSelect: (Uri) -> Unit,
	modifier: Modifier = Modifier,
) {
	LazyVerticalGrid(
		columns = GridCells.Fixed(count = 3),
		modifier = modifier
			.fillMaxWidth()
			.padding(top = 12.dp, start = 12.dp, end = 12.dp),
		horizontalArrangement = Arrangement.spacedBy(4.dp),
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		items(items = photos, key = { uri -> uri.toString() }) { uri ->
			PhotoTile(uri = uri, picked = uri == selected, onSelect = onSelect)
		}
	}
}

@Composable
private fun PhotoTile(
	uri: Uri,
	picked: Boolean,
	onSelect: (Uri) -> Unit,
) {
	val shape = RoundedCornerShape(8.dp)

	val mark = select(
		active = picked,
		on = palette.accentPrimary,
		off = Color.Transparent,
	)

	AsyncImage(
		model = uri,
		contentDescription = null,
		imageLoader = photoLoader(),
		contentScale = ContentScale.Crop,
		modifier = Modifier
			.aspectRatio(1f)
			.clip(shape)
			.border(width = 2.dp, color = mark, shape = shape)
			.clickable { onSelect(uri) },
	)
}

@Composable
private fun DoneButton(
	selected: Uri?,
	onChoose: (Uri) -> Unit,
) {
	val chosen = selected

	MUiButton(
		model = MUiButtonModel(
			style = MUiButtonStyle.Primary,
			label = stringResource(R.string.photo_done),
		),
		onClick = {
			if (chosen == null) {
				return@MUiButton
			}

			onChoose(chosen)
		},
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 12.dp)
			.then(
				if (chosen == null) {
					Modifier.alpha(alpha = 0.4f)
				} else {
					Modifier
				},
			),
	)
}

private fun recentPhotos(context: Context): List<Uri> {
	val photos = mutableListOf<Uri>()

	val projection = arrayOf(MediaStore.Images.Media._ID)

	val args = Bundle().apply {
		putInt(ContentResolver.QUERY_ARG_LIMIT, List(size = 60) { it }.size)

		putStringArray(
			ContentResolver.QUERY_ARG_SORT_COLUMNS,
			arrayOf(MediaStore.Images.Media.DATE_ADDED),
		)

		putInt(
			ContentResolver.QUERY_ARG_SORT_DIRECTION,
			ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
		)
	}

	context.contentResolver.query(
		MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		projection,
		args,
		null,
	)?.use { cursor ->
		val column = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

		while (cursor.moveToNext()) {
			photos += ContentUris.withAppendedId(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				cursor.getLong(column),
			)
		}
	}

	return photos
}