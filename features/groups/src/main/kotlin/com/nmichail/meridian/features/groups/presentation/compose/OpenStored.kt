package com.nmichail.meridian.features.groups.presentation.compose

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

internal fun Context.openStored(name: String, type: String, bytes: ByteArray) {
	val folder = File(cacheDir, "files").apply { mkdirs() }
	val safe = name.filter { char -> char.isLetterOrDigit() || char == '.' || char == '_' || char == '-' }
	val stored = File(folder, safe.ifEmpty { "file" })

	stored.writeBytes(bytes)

	val uri = FileProvider.getUriForFile(this, "$packageName.files", stored)
	val view = Intent(Intent.ACTION_VIEW).apply {
		setDataAndType(uri, type.ifEmpty { "application/octet-stream" })
		addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
	}

	try {
		startActivity(view)
	} catch (_: ActivityNotFoundException) {
		startActivity(Intent.createChooser(view, stored.name))
	}
}