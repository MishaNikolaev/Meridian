package com.nmichail.meridian.shared.chat.data.api

import com.nmichail.meridian.shared.chat.data.dto.AddMemberDto
import com.nmichail.meridian.shared.chat.data.dto.ChatDetailsDto
import com.nmichail.meridian.shared.chat.data.dto.PatchChatDto
import com.nmichail.meridian.shared.chat.data.dto.TransferChatDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface ChatEditApi {
	@PATCH("api/gateway/chats/{chatId}")
	suspend fun patchChat(@Path("chatId") chatId: String, @Body body: PatchChatDto): ChatDetailsDto

	@POST("api/gateway/chats/{chatId}/members")
	suspend fun addMember(@Path("chatId") chatId: String, @Body body: AddMemberDto)

	@POST("api/gateway/chats/{chatId}/leave")
	suspend fun leave(@Path("chatId") chatId: String)

	@DELETE("api/gateway/chats/{chatId}/members/{userId}")
	suspend fun removeMember(@Path("chatId") chatId: String, @Path("userId") userId: String)

	@POST("api/gateway/chats/{chatId}/transfer")
	suspend fun transfer(@Path("chatId") chatId: String, @Body body: TransferChatDto)

	@POST("api/gateway/chats/{chatId}/admins")
	suspend fun promote(@Path("chatId") chatId: String, @Body body: TransferChatDto)
}