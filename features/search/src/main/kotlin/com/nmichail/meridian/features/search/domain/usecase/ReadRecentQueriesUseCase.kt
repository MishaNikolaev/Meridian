package com.nmichail.meridian.features.search.domain.usecase

import com.nmichail.meridian.features.search.domain.repository.RecentQueryRepository
import javax.inject.Inject

class ReadRecentQueriesUseCase @Inject constructor(
	private val repository: RecentQueryRepository,
) {

	operator fun invoke(): List<String> = repository.read().queries()
}
