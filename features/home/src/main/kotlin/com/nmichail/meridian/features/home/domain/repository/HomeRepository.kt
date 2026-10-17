package com.nmichail.meridian.features.home.domain.repository

interface HomeRepository {

	fun text(): String

	fun save(text: String)
}
