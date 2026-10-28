package com.nmichail.meridian.shared.chat.data.message

import com.nmichail.meridian.core.network.error.GatewayCode
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.cipher.MessageCipher
import com.nmichail.meridian.shared.chat.data.cipher.encodeOpenedMessage
import com.nmichail.meridian.shared.chat.data.datasource.MemberDataSource
import com.nmichail.meridian.shared.chat.data.dto.DeviceMessageDto
import com.nmichail.meridian.shared.chat.data.dto.PostMessageDto
import java.util.UUID

internal suspend fun postMessage(
	api: ChatApi,
	members: MemberDataSource,
	chatId: String,
	text: String,
	replyAuthor: String,
	replyText: String,
	threadRoot: String,
	self: String?,
	deviceId: String?,
): PostMessageDto {

	val body = MessageCipher.encrypt(
		chatId = chatId,
		value = encodeOpenedMessage(
			text = text,
			replyAuthor = replyAuthor,
			replyText = replyText,
		),
	)

	val mine = deviceId ?: throw GatewayException(code = GatewayCode.DEVICE_NOT_FOUND)
	val others = mutableListOf<String>()
	for (member in api.getChat(chatId).members) {
		if (member.userId == self) {
			continue
		}
		for (device in members.getKeys(member.userId).devices) {
			others += device.deviceId
		}
	}

	return PostMessageDto(
		clientMessageId = UUID.randomUUID().toString(),
		deviceMessages = (others + mine).distinct().map { target ->
			DeviceMessageDto(
				deviceId = target,
				body = body,
			)
		},
		threadRoot = threadRoot.takeIf { it.isNotEmpty() },
	)
}