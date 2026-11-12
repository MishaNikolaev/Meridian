package com.nmichail.meridian.component.photopicker

import coil3.ImageLoader
import com.nmichail.meridian.core.network.config.NetworkConfig

private var loader: ImageLoader? = null

fun bindPhotoLoader(imageLoader: ImageLoader) {
	loader = imageLoader
}

fun photoLoader(): ImageLoader = checkNotNull(loader)

fun photoUrl(photoId: String): String {
	if (photoId.isEmpty()) {
		return ""
	}

	return "${NetworkConfig().baseUrl}api/gateway/media/$photoId"
}