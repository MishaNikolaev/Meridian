package com.nmichail.meridian.shared.chat.data.datasource

import android.content.Context
import android.content.SharedPreferences
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DeletionDataSourceImplTest {

	private val context: Context = mock()
	private val preferences: SharedPreferences = mock()
	private val editor: SharedPreferences.Editor = mock()
	private val dataSource by lazy { DeletionDataSourceImpl(context) }

	@Test
	fun `stored deleted messages EXPECT deleted messages`() {
		whenever(context.getSharedPreferences("meridian", Context.MODE_PRIVATE)) doReturn preferences
		whenever(preferences.getStringSet("deleted_messages", null)) doReturn setOf("message")

		val actual = dataSource.getDeleted()

		assertEquals(setOf("message"), actual)
	}

	@Test
	fun `missing deleted messages EXPECT no messages`() {
		whenever(context.getSharedPreferences("meridian", Context.MODE_PRIVATE)) doReturn preferences
		whenever(preferences.getStringSet("deleted_messages", null)) doReturn null
		whenever(preferences.getString("deleted_messages", null)) doReturn null

		val actual = dataSource.getDeleted()

		assertEquals(emptySet<String>(), actual)
	}

	@Test
	fun `deleted message text EXPECT deleted messages`() {
		whenever(context.getSharedPreferences("meridian", Context.MODE_PRIVATE)) doReturn preferences
		whenever(preferences.edit()) doReturn editor
		whenever(preferences.getStringSet("deleted_messages", null)) doThrow ClassCastException()
		whenever(preferences.getString("deleted_messages", null)) doReturn "message,other"

		val actual = dataSource.getDeleted()

		assertEquals(setOf("message", "other"), actual)
	}

	@Test
	fun `deleted message text EXPECT stored deleted messages`() {
		whenever(context.getSharedPreferences("meridian", Context.MODE_PRIVATE)) doReturn preferences
		whenever(preferences.edit()) doReturn editor
		whenever(preferences.getStringSet("deleted_messages", null)) doThrow ClassCastException()
		whenever(preferences.getString("deleted_messages", null)) doReturn "message,other"

		dataSource.getDeleted()

		verify(editor).putStringSet("deleted_messages", setOf("message", "other"))
	}

	@Test
	fun `save EXPECT stored message`() {
		whenever(context.getSharedPreferences("meridian", Context.MODE_PRIVATE)) doReturn preferences
		whenever(preferences.edit()) doReturn editor
		whenever(preferences.getStringSet("deleted_messages", null)) doReturn setOf("message")

		dataSource.saveDeleted("other")

		verify(editor).putStringSet("deleted_messages", setOf("message", "other"))
	}
}