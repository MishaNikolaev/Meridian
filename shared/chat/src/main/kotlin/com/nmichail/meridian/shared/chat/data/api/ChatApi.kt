package com.nmichail.meridian.shared.chat.data.api

import com.nmichail.meridian.shared.chat.data.model.ChatDetailsDto
import com.nmichail.meridian.shared.chat.data.model.ChatPageDto
import com.nmichail.meridian.shared.chat.data.model.CreateChatDto
import com.nmichail.meridian.shared.chat.data.model.KeyBundleDto
import com.nmichail.meridian.shared.chat.data.model.MessageDto
import com.nmichail.meridian.shared.chat.data.model.MessagePageDto
import com.nmichail.meridian.shared.chat.data.model.PostMessageDto
import com.nmichail.meridian.shared.chat.data.model.ReceiptDto
import com.nmichail.meridian.shared.chat.data.model.UserCardDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpStatement

interface ChatApi {

	@GET("api/gateway/chats")
	suspend fun getChats(): ChatPageDto

	@POST("api/gateway/chats")
	suspend fun create(@Body body: CreateChatDto): ChatDetailsDto

	@GET("api/gateway/chats/{chatId}")
	suspend fun getChat(@Path("chatId") chatId: String): ChatDetailsDto

	@GET("api/gateway/chats/{chatId}/messages")
	suspend fun getMessages(@Path("chatId") chatId: String): MessagePageDto

	@POST("api/gateway/chats/{chatId}/messages")
	suspend fun send(@Path("chatId") chatId: String, @Body body: PostMessageDto): MessageDto

	@POST("api/gateway/messages/{messageId}/receipts")
	suspend fun sendReceipt(@Path("messageId") messageId: String, @Body body: ReceiptDto): HttpStatement

	@GET("api/gateway/keys/bundle/{userId}")
	suspend fun getBundle(@Path("userId") userId: String): KeyBundleDto

	@GET("api/gateway/users")
	suspend fun getUsers(@Query("query") query: String): List<UserCardDto>
}
