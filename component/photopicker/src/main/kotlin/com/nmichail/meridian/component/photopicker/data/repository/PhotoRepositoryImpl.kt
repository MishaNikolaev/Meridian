package com.nmichail.meridian.component.photopicker.data.repository

import com.nmichail.meridian.component.photopicker.data.api.PhotoApi
import com.nmichail.meridian.component.photopicker.data.dto.AvatarBody
import com.nmichail.meridian.component.photopicker.domain.entity.PersonPhoto
import com.nmichail.meridian.component.photopicker.domain.repository.PhotoRepository
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.CancellationException

internal class PhotoRepositoryImpl(
	private val api: PhotoApi,
) : PhotoRepository {
	override suspend fun installPhoto(bytes: ByteArray, contentType: String): String {
		val photoId = upload(bytes, contentType)

		val saved = api.saveAvatar(AvatarBody(photoId = photoId))

		return saved.photoId ?: photoId
	}

	override suspend fun upload(bytes: ByteArray, contentType: String): String =
		api.upload(file(bytes, contentType)).blobId

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

	private fun file(bytes: ByteArray, contentType: String): MultiPartFormDataContent = MultiPartFormDataContent(
		formData {
			append(
				key = "file",
				value = bytes,
				headers = Headers.build {
					append(HttpHeaders.ContentType, contentType)

					append(HttpHeaders.ContentDisposition, "filename=\"photo\"")
				},
			)
		},
	)
}
