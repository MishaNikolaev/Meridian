package com.nmichail.meridian.features.calls.domain.repository

import com.nmichail.meridian.features.calls.domain.entity.CallRecord

interface CallsRepository {
	suspend fun loadCalls(): List<CallRecord>
}