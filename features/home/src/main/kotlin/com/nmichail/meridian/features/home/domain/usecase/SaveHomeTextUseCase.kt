package com.nmichail.meridian.features.home.domain.usecase

import com.nmichail.meridian.features.home.domain.repository.HomeRepository
import javax.inject.Inject

class SaveHomeTextUseCase @Inject constructor(
	private val repository: HomeRepository,
) : (String) -> Unit by repository::save
