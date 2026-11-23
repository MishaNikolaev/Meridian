package com.nmichail.meridian.component.photopicker

import com.nmichail.meridian.component.photopicker.data.api.createPhotoApi
import de.jensklingenberg.ktorfit.Ktorfit
import kotlinx.coroutines.CancellationException

data class PersonFace(
	val name: String,
	val photoId: String,
)

suspend fun ensurePersonPhoto(ktorfit: Ktorfit, userId: String): String =
	loadPersonFace(ktorfit, userId).photoId

suspend fun loadPersonFace(ktorfit: Ktorfit, userId: String): PersonFace {
	if (userId.isEmpty()) {
		return PersonFace(name = "", photoId = "")
	}
	val stored = PhotoLocalDataSource.storedPersonPhoto(userId)
	return try {
		val card = ktorfit.createPhotoApi().publicPhoto(userId)
		val photoId = card.photoId.orEmpty().ifEmpty { stored }
		if (photoId.isNotEmpty()) {
			PhotoLocalDataSource.keepPersonPhoto(userId = userId, photoId = photoId)
		}
		PersonFace(name = card.displayName, photoId = photoId)
	} catch (error: CancellationException) {
		throw error
	} catch (_: Exception) {
		PersonFace(name = "", photoId = stored)
	}
}
