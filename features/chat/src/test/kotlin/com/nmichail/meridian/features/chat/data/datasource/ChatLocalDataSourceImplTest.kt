package com.nmichail.meridian.features.chat.data.datasource

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

class ChatLocalDataSourceImplTest {

	private val context: Context = mock()
	private val preferences: SharedPreferences = mock()
	private val editor: SharedPreferences.Editor = mock()
	private val dataSource by lazy { ChatLocalDataSourceImpl(context) }

	@BeforeEach
	fun setUp() {
		whenever(context.getSharedPreferences(any(), any())) doReturn preferences
		whenever(preferences.edit()) doReturn editor
		whenever(editor.putString(any(), any())) doReturn editor
	}

	@Test
	fun `stored text EXPECT text`() {
		whenever(preferences.getString("home_text", null)) doReturn "текст"

		val actual = dataSource.getText()

		assertEquals("текст", actual)
	}

	@Test
	fun `missing text EXPECT empty text`() {
		whenever(preferences.getString("home_text", null)) doReturn null

		val actual = dataSource.getText()

		assertEquals("", actual)
	}

	@Test
	fun `save EXPECT stored text`() {
		dataSource.save("текст")

		verify(editor).putString("home_text", "текст")
	}
}