package com.nmichail.meridian.shared.chat.data.datasource

import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.dto.KeyBundleDto
import com.nmichail.meridian.shared.chat.data.dto.UserCardDto
import javax.inject.Inject

interface MemberDataSource {

	suspend fun getUsers(query: String): List<UserCardDto>

	suspend fun getKeys(userId: String): KeyBundleDto
}

class MemberDataSourceImpl @Inject constructor(
	private val api: ChatApi,
) : MemberDataSource {

	override suspend fun getUsers(query: String): List<UserCardDto> {
		return api.getUsers(query)
	}

	override suspend fun getKeys(userId: String): KeyBundleDto {
		return api.getKeys(userId)
	}
}