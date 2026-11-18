package com.nmichail.meridian.features.conversation.presentation.compose.component

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

internal data class RecentFile(
	val uri: Uri,
	val name: String,
	val video: Boolean,
	val image: Boolean,
	val bytes: Long,
)

@Suppress("MagicNumber")
internal fun Context.recentFiles(images: Boolean): List<RecentFile> {
	val folder = MediaStore.Files.getContentUri("external")
	val kind = MediaStore.Files.FileColumns.MEDIA_TYPE
	val kinds = if (images) {
		listOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
	} else {
		listOf(
			MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
			MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
			MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO,
			MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT,
		)
	}
	val selection = kinds.joinToString(separator = " OR ") { "$kind=?" }
	val args = kinds.map { type -> type.toString() }.toTypedArray()

	val rows = try {
		contentResolver.query(
			folder,
			arrayOf(
				MediaStore.MediaColumns._ID,
				MediaStore.MediaColumns.DISPLAY_NAME,
				MediaStore.MediaColumns.SIZE,
				MediaStore.Files.FileColumns.MEDIA_TYPE,
			),
			selection,
			args,
			"${MediaStore.MediaColumns.DATE_ADDED} DESC",
		)
	} catch (_: SecurityException) {
		null
	}

	val found = mutableListOf<RecentFile>()

	rows?.use { cursor ->
		val idColumn = cursor.getColumnIndex(MediaStore.MediaColumns._ID)
		val nameColumn = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
		val sizeColumn = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)
		val typeColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)

		while (cursor.moveToNext() && found.size < 60 && idColumn >= 0) {
			val name = if (nameColumn >= 0) cursor.getString(nameColumn).orEmpty() else ""
			val type = if (typeColumn >= 0) cursor.getInt(typeColumn) else 0
			val video = type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
			val image = type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
			val collection = when {
				video -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
				image -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				else -> folder
			}
			found += RecentFile(
				uri = ContentUris.withAppendedId(collection, cursor.getLong(idColumn)),
				name = name.ifEmpty { "file" },
				video = video,
				image = image,
				bytes = if (sizeColumn >= 0) cursor.getLong(sizeColumn) else 0L,
			)
		}
	}

	return found
}