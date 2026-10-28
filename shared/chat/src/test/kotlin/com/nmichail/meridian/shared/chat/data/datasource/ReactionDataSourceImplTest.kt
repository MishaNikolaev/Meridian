package com.nmichail.meridian.shared.chat.data.datasource

import android.content.Context
import android.content.SharedPreferences
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ReactionDataSourceImplTest {

	private val context: Context = mock()
	private val preferences: SharedPreferences = mock()
	private val editor: SharedPreferences.Editor = mock()
	private lateinit var source: ReactionDataSourceImpl

	@BeforeEach
	fun setUp() {
		whenever(context.getSharedPreferences(any(), any())) doReturn preferences
		whenever(preferences.edit()) doReturn editor
		whenever(editor.putString(any(), any())) doReturn editor
		source = ReactionDataSourceImpl(context)
	}

	@Test
	fun `get empty EXPECT no reactions`() {
		whenever(preferences.getString("message_reactions", null)) doReturn null

		assertEquals(emptyMap<String, String>(), source.getReactions())
	}

	@Test
	fun `save EXPECT saved reaction`() {
		whenever(preferences.getString("message_reactions", null)) doReturn null

		source.saveReaction("message", "LIKE")

		verify(editor).putString("message_reactions", """{"message":"LIKE"}""")
	}
}
