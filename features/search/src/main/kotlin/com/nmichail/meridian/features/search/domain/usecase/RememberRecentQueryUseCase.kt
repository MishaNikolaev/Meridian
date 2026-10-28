package com.nmichail.meridian.features.search.domain.usecase

import com.nmichail.meridian.features.search.domain.repository.RecentQueryRepository
import javax.inject.Inject

class RememberRecentQueryUseCase @Inject constructor(
	private val repository: RecentQueryRepository,
) {

	operator fun invoke(query: String) {
		val trimmed = query.trim()
		if (trimmed.isEmpty()) {
			return
		}
		val next = listOf(trimmed) + repository.read().queries().filter { item -> item != trimmed }
		repository.save(next.take(n = 8).joinToString("\n"))
	}
}
