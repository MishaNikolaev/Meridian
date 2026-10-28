package com.nmichail.meridian.shared.chat.data.datasource

import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.dto.KeyDto
import com.nmichail.meridian.shared.chat.data.dto.UserCardDto
import javax.inject.Inject

interface MemberDataSource {

	suspend fun getUsers(query: String): List<UserCardDto>

	suspend fun getKeys(userId: String): KeyDto
}

class MemberDataSourceImpl @Inject constructor(
	private val api: ChatApi,
) : MemberDataSource {

	override suspend fun getUsers(query: String): List<UserCardDto> {
		return api.getUsers(query)
	}

	override suspend fun getKeys(userId: String): KeyDto {
		return api.getKeys(userId)
	}
}