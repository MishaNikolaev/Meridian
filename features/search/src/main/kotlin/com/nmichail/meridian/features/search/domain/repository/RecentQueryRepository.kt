package com.nmichail.meridian.features.search.domain.repository

interface RecentQueryRepository {

	fun read(): String?

	fun save(value: String)
}
