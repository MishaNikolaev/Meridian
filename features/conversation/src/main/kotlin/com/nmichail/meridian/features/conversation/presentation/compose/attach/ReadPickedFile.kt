package com.nmichail.meridian.features.conversation.presentation.compose.attach

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

internal fun Context.readPickedFile(uri: Uri): PickedFile? {
	val name = displayName(uri)
	val resolved = contentResolver.getType(uri).orEmpty().fileType(name)

	if (resolved.isEmpty()) {
		return null
	}

	val bytes = contentResolver.openInputStream(uri)?.use { stream -> stream.readBytes() } ?: return null

	return PickedFile(name = name, type = resolved, bytes = bytes)
}

private fun Context.displayName(uri: Uri): String {
	val cursor = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)

	cursor?.use { rows ->
		if (rows.moveToFirst()) {
			val index = rows.getColumnIndex(OpenableColumns.DISPLAY_NAME)

			if (index >= 0) {
				return rows.getString(index).orEmpty().ifEmpty { "file" }
			}
		}
	}

	return uri.lastPathSegment ?: "file"
}

private fun String.fileType(name: String): String {
	val mime = substringBefore(';').trim().lowercase().let { raw ->
		if (raw == "image/jpg") "image/jpeg" else raw
	}

	if (mime.accepted()) {
		return mime
	}

	val extension = name.substringAfterLast('.', "").lowercase()

	return extensions[extension].orEmpty()
}

private fun String.accepted(): Boolean = when (this) {
	"image/jpeg", "image/png", "application/pdf", "video/mp4", "video/webm", "video/quicktime" -> true
	else -> startsWith("audio/")
}

private val extensions = mapOf(
	"jpg" to "image/jpeg",
	"jpeg" to "image/jpeg",
	"png" to "image/png",
	"pdf" to "application/pdf",
	"mp3" to "audio/mpeg",
	"m4a" to "audio/mp4",
	"aac" to "audio/aac",
	"ogg" to "audio/ogg",
	"wav" to "audio/wav",
	"mp4" to "video/mp4",
	"mov" to "video/quicktime",
	"webm" to "video/webm",
)
