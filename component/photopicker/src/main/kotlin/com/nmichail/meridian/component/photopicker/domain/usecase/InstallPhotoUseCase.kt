package com.nmichail.meridian.component.photopicker.domain.usecase

import com.nmichail.meridian.component.photopicker.domain.repository.PhotoRepository
import javax.inject.Inject

class InstallPhotoUseCase @Inject constructor(
	private val repository: PhotoRepository,
) : suspend (ByteArray, String) -> String by repository::installPhoto