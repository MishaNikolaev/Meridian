package com.nmichail.meridian.component.photopicker.compose

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

internal fun Bitmap.toJpeg(): PickedPhoto? = encode(
	format = Bitmap.CompressFormat.JPEG,
	quality = 85,
	type = "image/jpeg",
)

internal fun Bitmap.toPng(): PickedPhoto? = encode(
	format = Bitmap.CompressFormat.PNG,
	quality = 100,
	type = "image/png",
)

private fun Bitmap.encode(
	format: Bitmap.CompressFormat,
	quality: Int,
	type: String,
): PickedPhoto? {
	val bytes = ByteArrayOutputStream().use { output ->
		val written = compress(format, quality, output)

		if (written) output.toByteArray() else ByteArray(size = 0)
	}

	if (bytes.isEmpty()) {
		return null
	}

	return PickedPhoto(bytes = bytes, type = type)
}