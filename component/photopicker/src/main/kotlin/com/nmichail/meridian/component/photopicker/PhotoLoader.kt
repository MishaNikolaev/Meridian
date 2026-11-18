package com.nmichail.meridian.component.photopicker

import android.content.Context
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.size.Size
import com.nmichail.meridian.core.network.config.NetworkConfig

private var loader: ImageLoader? = null

fun bindPhotoLoader(imageLoader: ImageLoader) {
	loader = imageLoader
}

fun photoLoader(): ImageLoader = checkNotNull(loader)

fun photoRequest(context: Context, photoId: String, pixels: Int): ImageRequest = ImageRequest.Builder(context)
	.data(photoUrl(photoId))
	.size(pixels)
	.diskCacheKey("$photoId:$pixels")
	.memoryCacheKey("$photoId:$pixels")
	.build()

fun fullPhotoRequest(context: Context, photoId: String): ImageRequest = ImageRequest.Builder(context)
	.data(photoUrl(photoId))
	.size(Size.ORIGINAL)
	.diskCacheKey("$photoId:full")
	.memoryCacheKey("$photoId:full")
	.build()

fun photoUrl(photoId: String): String {
	if (photoId.isEmpty()) {
		return ""
	}

	return "${NetworkConfig().baseUrl}api/gateway/media/$photoId"
}