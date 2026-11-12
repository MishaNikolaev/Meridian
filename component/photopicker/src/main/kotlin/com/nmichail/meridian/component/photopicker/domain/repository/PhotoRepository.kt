package com.nmichail.meridian.component.photopicker.domain.repository

import com.nmichail.meridian.component.photopicker.domain.entity.PersonPhoto

interface PhotoRepository {

	suspend fun installPhoto(bytes: ByteArray, contentType: String): String

	suspend fun upload(bytes: ByteArray, contentType: String): String

	suspend fun loadPhoto(userId: String): PersonPhoto?
}