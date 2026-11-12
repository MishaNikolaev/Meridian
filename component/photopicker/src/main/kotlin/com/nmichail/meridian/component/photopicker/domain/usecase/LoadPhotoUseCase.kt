package com.nmichail.meridian.component.photopicker.domain.usecase

import com.nmichail.meridian.component.photopicker.domain.entity.PersonPhoto
import com.nmichail.meridian.component.photopicker.domain.repository.PhotoRepository
import javax.inject.Inject

class LoadPhotoUseCase @Inject constructor(
	private val repository: PhotoRepository,
) : suspend (String) -> PersonPhoto? by repository::loadPhoto