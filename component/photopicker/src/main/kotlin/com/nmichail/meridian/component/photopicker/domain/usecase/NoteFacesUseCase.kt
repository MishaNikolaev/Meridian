package com.nmichail.meridian.component.photopicker.domain.usecase

import com.nmichail.meridian.component.photopicker.domain.repository.PhotoRepository
import javax.inject.Inject

class NoteFacesUseCase @Inject constructor(
	private val repository: PhotoRepository,
) {
	suspend operator fun invoke(ids: List<String>) {
		ids.filter { id -> id.isNotEmpty() }.distinct().forEach { id ->
			val photoId = repository.loadPhoto(id)?.photoId.orEmpty()

			repository.keepPersonPhoto(userId = id, photoId = photoId)
		}
	}
}