package com.nmichail.meridian.features.groups.presentation.compose

import androidx.compose.runtime.Composable
import com.nmichail.meridian.component.photo.MediaItem
import com.nmichail.meridian.component.photo.MediaPager
import com.nmichail.meridian.features.groups.presentation.GroupFile

@Composable
internal fun GroupGallery(
	files: List<GroupFile>,
	photoId: String,
	onClose: () -> Unit,
	onPlay: (GroupFile) -> Unit,
) {
	val items = files.mapNotNull { file -> file.mediaItem() }

	MediaPager(
		items = items,
		currentId = photoId,
		onClose = onClose,
		onPlay = { item ->
			val file = files.firstOrNull { entry -> entry.id == item.id } ?: return@MediaPager

			onPlay(file)
		},
	)
}

private fun GroupFile.mediaItem(): MediaItem? = when {
	type.startsWith("image/") -> MediaItem(id = id, name = name, type = type, video = false)
	type.startsWith("video/") -> MediaItem(id = id, name = name, type = type, video = true)
	else -> null
}