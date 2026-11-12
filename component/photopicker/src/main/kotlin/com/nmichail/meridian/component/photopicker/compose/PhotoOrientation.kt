package com.nmichail.meridian.component.photopicker.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayInputStream

private const val SAMPLE = 2
private const val FLIP = -1f
private const val TURN_90 = 90f
private const val TURN_180 = 180f
private const val TURN_270 = 270f

internal fun Context.orientedPhoto(uri: Uri): ImageBitmap? {
	val bytes = contentResolver.openInputStream(uri)?.use { stream -> stream.readBytes() } ?: return null

	val bitmap = orientedBitmap(bytes) ?: return null

	return bitmap.asImageBitmap()
}

private fun orientedBitmap(bytes: ByteArray): Bitmap? {
	val orientation = exifOrientation(bytes)

	val sampled = decodeSampled(bytes) ?: return null

	return turn(sampled, orientation)
}

private fun exifOrientation(bytes: ByteArray): Int = runCatching {
	ExifInterface(ByteArrayInputStream(bytes)).getAttributeInt(
		ExifInterface.TAG_ORIENTATION,
		ExifInterface.ORIENTATION_UNDEFINED,
	)
}.getOrDefault(ExifInterface.ORIENTATION_UNDEFINED)

private fun decodeSampled(bytes: ByteArray): Bitmap? {
	val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }

	BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)

	val options = BitmapFactory.Options().apply {
		inSampleSize = sample(
			width = bounds.outWidth,
			height = bounds.outHeight,
			limit = 2048,
		)
	}

	return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
}

private fun sample(width: Int, height: Int, limit: Int): Int {
	var size = 1

	var edge = maxOf(width, height)

	while (edge / SAMPLE >= limit) {
		edge /= SAMPLE

		size *= SAMPLE
	}

	return size
}

private fun turn(bitmap: Bitmap, orientation: Int): Bitmap {
	val matrix = Matrix()

	when (orientation) {
		ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(TURN_90)
		ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(TURN_180)
		ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(TURN_270)
		ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(FLIP, 1f)
		ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, FLIP)
		ExifInterface.ORIENTATION_TRANSPOSE -> {
			matrix.postRotate(TURN_90)
			matrix.postScale(FLIP, 1f)
		}
		ExifInterface.ORIENTATION_TRANSVERSE -> {
			matrix.postRotate(TURN_270)
			matrix.postScale(FLIP, 1f)
		}
		else -> return bitmap
	}

	val turned = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

	if (turned !== bitmap) {
		bitmap.recycle()
	}

	return turned
}
