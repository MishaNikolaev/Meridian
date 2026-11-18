package com.nmichail.meridian.component.photopicker.data.repository

import com.nmichail.meridian.component.photopicker.PhotoLocalDataSource
import com.nmichail.meridian.component.photopicker.data.api.PhotoApi
import com.nmichail.meridian.component.photopicker.data.photoUpload
import com.nmichail.meridian.component.photopicker.data.dto.AvatarBody
import com.nmichail.meridian.component.photopicker.domain.entity.PersonPhoto
import com.nmichail.meridian.component.photopicker.domain.repository.PhotoRepository
import com.nmichail.meridian.component.photopicker.photoUrl
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.CancellationException

internal class PhotoRepositoryImpl(
	private val api: PhotoApi,
	private val client: HttpClient,
) : PhotoRepository {
	override suspend fun installPhoto(bytes: ByteArray, contentType: String): String {
		val photoId = uploadPhoto(bytes, contentType)

		val saved = api.saveAvatar(AvatarBody(photoId = photoId))

		return saved.photoId ?: photoId
	}

	override suspend fun uploadPhoto(bytes: ByteArray, contentType: String): String =
		api.upload(photoUpload(bytes, contentType)).blobId

	override suspend fun downloadPhoto(blobId: String): ByteArray = client.get(photoUrl(blobId)).bodyAsBytes()

	override fun keepPersonPhoto(userId: String, photoId: String) {
		PhotoLocalDataSource.keepPersonPhoto(userId = userId, photoId = photoId)
	}

	override suspend fun loadPhoto(userId: String): PersonPhoto? {
		try {
			val card = api.publicPhoto(userId)

			return PersonPhoto(
				name = card.displayName,
				photoId = card.photoId.orEmpty(),
			)
		} catch (exception: CancellationException) {
			throw exception
		} catch (_: Exception) {
			return null
		}
	}
}
