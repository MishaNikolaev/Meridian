package com.nmichail.meridian.features.chat.data.repository

import com.nmichail.meridian.features.chat.data.datasource.ChatLocalDataSource
import com.nmichail.meridian.features.chat.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
	private val dataSource: ChatLocalDataSource,
) : ChatRepository {

	override fun getText(): String = dataSource.getText()

	override fun save(text: String) {
		dataSource.save(text)
	}
}
