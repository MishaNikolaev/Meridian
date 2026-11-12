package com.nmichail.meridian.shared.chat.data.datasource

import android.content.SharedPreferences
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ReactionDataSourceImplTest {
	private val preferences: SharedPreferences = mock()
	private val source = ReactionDataSourceImpl(preferences)

	@Test
	fun `get empty EXPECT no reactions`() {
		whenever(preferences.getString("message_reactions", null)) doReturn null

		assertEquals(emptyMap<String, Set<String>>(), source.getReactions())
	}

	@Test
	fun `save EXPECT saved reaction`() {
		val editor: SharedPreferences.Editor = mock()
		whenever(preferences.getString("message_reactions", null)) doReturn null
		whenever(preferences.edit()) doReturn editor
		whenever(editor.putString(any(), any())) doReturn editor

		source.saveReactions("message", setOf("LIKE"))

		verify(editor).putString("message_reactions", """{"message":["LIKE"]}""")
	}

	@Test
	fun `legacy string EXPECT set`() {
		whenever(preferences.getString("message_reactions", null)) doReturn """{"message":"LIKE"}"""

		assertEquals(mapOf("message" to setOf("LIKE")), source.getReactions())
	}
}
