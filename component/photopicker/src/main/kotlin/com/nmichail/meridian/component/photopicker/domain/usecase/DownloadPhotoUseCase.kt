package com.nmichail.meridian.component.photopicker.domain.usecase

import com.nmichail.meridian.component.photopicker.domain.repository.PhotoRepository
import javax.inject.Inject

class DownloadPhotoUseCase @Inject constructor(
	private val repository: PhotoRepository,
) : suspend (String) -> ByteArray by repository::downloadPhoto