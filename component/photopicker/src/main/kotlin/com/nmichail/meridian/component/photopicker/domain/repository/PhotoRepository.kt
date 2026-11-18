package com.nmichail.meridian.component.photopicker.domain.repository

import com.nmichail.meridian.component.photopicker.domain.entity.PersonPhoto

interface PhotoRepository {

	suspend fun installPhoto(bytes: ByteArray, contentType: String): String

	suspend fun uploadPhoto(bytes: ByteArray, contentType: String): String

	suspend fun downloadPhoto(blobId: String): ByteArray

	suspend fun loadPhoto(userId: String): PersonPhoto?

	fun keepPersonPhoto(userId: String, photoId: String)
}