package com.nmichail.meridian.features.calls.catalog.domain.usecase

import com.nmichail.meridian.features.calls.catalog.domain.entity.CallRecord
import com.nmichail.meridian.features.calls.catalog.domain.repository.CallsRepository
import javax.inject.Inject

class LoadCallsUseCase @Inject constructor(
	private val repository: CallsRepository,
) : suspend () -> List<CallRecord> by repository::loadCalls