package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.runtime.Composable
import com.nmichail.meridian.component.photo.MediaItem
import com.nmichail.meridian.component.photo.MediaPager
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

@Composable
internal fun ChatGallery(
	lines: List<ChatLine>,
	photoId: String,
	onClose: () -> Unit,
	onPlay: (String, String, String) -> Unit,
) {
	val items = lines.mapNotNull { line -> line.mediaItem() }

	MediaPager(
		items = items,
		currentId = photoId,
		onClose = onClose,
		onPlay = { item -> onPlay(item.id, item.name, item.type) },
	)
}

private fun ChatLine.mediaItem(): MediaItem? {
	if (deleted || fileId.isEmpty()) {
		return null
	}

	return when {
		fileType.startsWith("image/") -> MediaItem(id = fileId, name = fileName, type = fileType, video = false)
		fileType.startsWith("video/") -> MediaItem(id = fileId, name = fileName, type = fileType, video = true)
		else -> null
	}
}