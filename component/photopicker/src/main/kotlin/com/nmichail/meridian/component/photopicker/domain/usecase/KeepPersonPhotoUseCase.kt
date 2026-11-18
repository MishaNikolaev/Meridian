package com.nmichail.meridian.component.photopicker.domain.usecase

import com.nmichail.meridian.component.photopicker.domain.repository.PhotoRepository
import javax.inject.Inject

class KeepPersonPhotoUseCase @Inject constructor(
	private val repository: PhotoRepository,
) : (String, String) -> Unit by repository::keepPersonPhoto
