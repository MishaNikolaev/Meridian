package com.nmichail.meridian.shared.chat.data.api

import com.nmichail.meridian.shared.chat.data.dto.ChatDetailsDto
import com.nmichail.meridian.shared.chat.data.dto.ChatPageDto
import com.nmichail.meridian.shared.chat.data.dto.CreateChatDto
import com.nmichail.meridian.shared.chat.data.dto.KeyBundleDto
import com.nmichail.meridian.shared.chat.data.dto.MessageDto
import com.nmichail.meridian.shared.chat.data.dto.MessagePageDto
import com.nmichail.meridian.shared.chat.data.dto.PostMessageDto
import com.nmichail.meridian.shared.chat.data.dto.ReactionRequestDto
import com.nmichail.meridian.shared.chat.data.dto.ReadRequestDto
import com.nmichail.meridian.shared.chat.data.dto.UserCardDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpStatement

interface ChatApi {

	@GET("api/gateway/chats")
	suspend fun getChats(): ChatPageDto

	@POST("api/gateway/chats")
	suspend fun createChat(@Body body: CreateChatDto): ChatDetailsDto

	@GET("api/gateway/chats/{chatId}")
	suspend fun getChat(@Path("chatId") chatId: String): ChatDetailsDto

	@GET("api/gateway/chats/{chatId}/messages")
	suspend fun getMessages(@Path("chatId") chatId: String): MessagePageDto

	@POST("api/gateway/chats/{chatId}/messages")
	suspend fun sendMessages(@Path("chatId") chatId: String, @Body body: PostMessageDto): MessageDto

	@DELETE("api/gateway/messages/{messageId}")
	suspend fun deleteMessages(@Path("messageId") messageId: String): HttpStatement

	@POST("api/gateway/messages/{messageId}/read")
	suspend fun markRead(@Path("messageId") messageId: String, @Body body: ReadRequestDto): HttpStatement

	@PUT("api/gateway/messages/{messageId}/reaction")
	suspend fun setReaction(@Path("messageId") messageId: String, @Body body: ReactionRequestDto): HttpStatement

	@GET("api/gateway/keys/{userId}")
	suspend fun getKeys(@Path("userId") userId: String): KeyBundleDto

	@GET("api/gateway/users")
	suspend fun getUsers(@Query("query") query: String): List<UserCardDto>
}