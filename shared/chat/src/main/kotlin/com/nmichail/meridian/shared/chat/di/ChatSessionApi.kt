package com.nmichail.meridian.shared.chat.di

import com.nmichail.meridian.shared.chat.data.datasource.ChatDataSource
import com.nmichail.meridian.shared.chat.data.datasource.MessageDataSource
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import com.nmichail.meridian.shared.chat.domain.repository.GroupEditRepository
import com.nmichail.meridian.shared.chat.domain.repository.ReactionRepository

interface ChatSessionApi {

	fun chatRepository(): ChatRepository

	fun groupEditRepository(): GroupEditRepository

	fun reactionRepository(): ReactionRepository

	fun messageDataSource(): MessageDataSource

	fun chatDataSource(): ChatDataSource
}