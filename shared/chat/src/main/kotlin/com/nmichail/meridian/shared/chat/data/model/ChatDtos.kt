package com.nmichail.meridian.shared.chat.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class ChatTypeDto {
	DIRECT,
	GROUP,
	CHANNEL,
}

@Serializable
data class UserCardDto(
	val userId: String,
	val username: String,
	val displayName: String,
)

@Serializable
data class CreateChatDto(
	val type: ChatTypeDto,
	val title: String? = null,
	val memberIds: List<String> = emptyList(),
)

@Serializable
data class CipherEnvelopeDto(
	val recipientDeviceId: String,
	val body: String,
)

@Serializable
enum class ReceiptKindDto {
	READ,
}

@Serializable
data class ReceiptDto(
	val kind: ReceiptKindDto,
)

@Serializable
data class PostMessageDto(
	val clientMessageId: String,
	val envelopes: List<CipherEnvelopeDto>,
)

@Serializable
data class MessageDto(
	val messageId: String,
	val chatId: String,
	val senderUserId: String,
	val createdAt: String,
	val deleted: Boolean = false,
	val envelopes: List<CipherEnvelopeDto> = emptyList(),
)

@Serializable
data class ChatSummaryDto(
	val chatId: String,
	val type: ChatTypeDto,
	val title: String? = null,
	val peerUserId: String? = null,
	val unreadCount: Int = 0,
	val lastMessage: MessageDto? = null,
)

@Serializable
data class ChatMemberDto(
	val userId: String,
	val username: String,
	val displayName: String,
)

@Serializable
data class ChatDetailsDto(
	val chat: ChatSummaryDto,
	val members: List<ChatMemberDto>,
)

@Serializable
data class ChatPageDto(
	val items: List<ChatSummaryDto> = emptyList(),
)

@Serializable
data class MessagePageDto(
	val items: List<MessageDto> = emptyList(),
)

@Serializable
data class DeviceBundleDto(
	val deviceId: String,
)

@Serializable
data class KeyBundleDto(
	val userId: String,
	val devices: List<DeviceBundleDto> = emptyList(),
)
