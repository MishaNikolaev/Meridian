package com.nmichail.meridian.shared.chat.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.datasource.ReactionDataSourceImpl
import com.nmichail.meridian.shared.chat.data.dto.ReactionRequestDto
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ReactionRepositoryImplTest {

	private val api: ChatApi = mock()
	private val context: Context = mock()
	private val preferences: SharedPreferences = mock()
	private val editor: SharedPreferences.Editor = mock()
	private lateinit var repository: ReactionRepositoryImpl

	@BeforeEach
	fun setUp() {
		whenever(context.getSharedPreferences(any(), any())) doReturn preferences
		whenever(preferences.edit()) doReturn editor
		whenever(editor.putString(any(), any())) doReturn editor
		repository = ReactionRepositoryImpl(
			api = api,
			reactionDataSource = ReactionDataSourceImpl(context),
		)
	}

	@Test
	fun `save reaction EXPECT stored`() = runTest {
		whenever(preferences.getString("message_reactions", null)) doReturn null

		repository.saveReaction(
			messageId = "message",
			reactions = setOf("LIKE"),
			saveUsed = false,
		)

		verify(editor).putString("message_reactions", """{"message":["LIKE"]}""")
	}

	@Test
	fun `react EXPECT reaction request`() = runTest {
		whenever(
			api.setReaction(
				"message",
				ReactionRequestDto(reaction = "LIKE", active = true),
			),
		) doReturn mock<HttpStatement>()

		repository.react(
			messageId = "message",
			reaction = "LIKE",
			active = true,
		)

		verify(api).setReaction(
			"message",
			ReactionRequestDto(reaction = "LIKE", active = true),
		)
	}
}