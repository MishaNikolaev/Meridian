package com.nmichail.meridian.features.search.domain.usecase

import com.nmichail.meridian.features.search.domain.repository.RecentQueryRepository
import javax.inject.Inject

class RecentQueriesUseCase @Inject constructor(
	private val recentQueryRepository: RecentQueryRepository,
) {

	private companion object {

		const val LIMIT = 8
	}

	fun read(): List<String> = recentQueryRepository.read()
		.orEmpty()
		.split("\n")
		.filter { query -> query.isNotEmpty() }

	fun remember(query: String) {
		val trimmed = query.trim()
		if (trimmed.isEmpty()) {
			return
		}
		val next = listOf(trimmed) + read().filter { item -> item != trimmed }
		recentQueryRepository.save(next.take(LIMIT).joinToString("\n"))
	}

	fun forget(query: String) {
		val next = read().filter { item -> item != query }
		recentQueryRepository.save(next.joinToString("\n"))
	}
}
