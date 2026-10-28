package com.nmichail.meridian.features.search.domain.usecase

import com.nmichail.meridian.features.search.domain.repository.RecentQueryRepository
import javax.inject.Inject

class ForgetRecentQueryUseCase @Inject constructor(
	private val repository: RecentQueryRepository,
) {

	operator fun invoke(query: String) {
		val next = repository.read().queries().filter { item -> item != query }
		repository.save(next.joinToString("\n"))
	}
}
