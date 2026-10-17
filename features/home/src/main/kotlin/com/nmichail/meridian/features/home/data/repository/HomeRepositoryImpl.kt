package com.nmichail.meridian.features.home.data.repository

import com.nmichail.meridian.features.home.data.datasource.HomeLocalDataSource
import com.nmichail.meridian.features.home.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
	private val dataSource: HomeLocalDataSource,
) : HomeRepository {

	override fun text(): String = dataSource.text()

	override fun save(text: String) {
		dataSource.save(text)
	}
}
