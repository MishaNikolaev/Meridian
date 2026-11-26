package com.nmichail.meridian.features.calls.catalog.domain.repository

import com.nmichail.meridian.features.calls.catalog.domain.entity.CallRecord

interface CallsRepository {
	suspend fun loadCalls(): List<CallRecord>
}