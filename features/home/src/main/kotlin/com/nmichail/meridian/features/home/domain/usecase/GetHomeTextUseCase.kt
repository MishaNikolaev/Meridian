package com.nmichail.meridian.features.home.domain.usecase

import com.nmichail.meridian.features.home.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeTextUseCase @Inject constructor(
	private val repository: HomeRepository,
) : () -> String by repository::text
