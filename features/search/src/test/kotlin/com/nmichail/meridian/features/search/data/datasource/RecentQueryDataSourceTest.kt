package com.nmichail.meridian.features.search.data.datasource

import android.content.Context
import android.content.SharedPreferences
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RecentQueryDataSourceTest {

	private val context: Context = mock()
	private val preferences: SharedPreferences = mock()
	private val editor: SharedPreferences.Editor = mock()
	private val dataSource by lazy { RecentQueryDataSource(context) }

	@Test
	fun `stored query EXPECT query`() {
		whenever(context.getSharedPreferences("meridian", Context.MODE_PRIVATE)) doReturn preferences
		whenever(preferences.getString("search_recent", null)) doReturn "пуши"

		val actual = dataSource.read()

		assertEquals("пуши", actual)
	}

	@Test
	fun `missing query EXPECT no query`() {
		whenever(context.getSharedPreferences("meridian", Context.MODE_PRIVATE)) doReturn preferences
		whenever(preferences.getString("search_recent", null)) doReturn null

		val actual = dataSource.read()

		assertEquals(null, actual)
	}

	@Test
	fun `save EXPECT stored query`() {
		whenever(context.getSharedPreferences("meridian", Context.MODE_PRIVATE)) doReturn preferences
		whenever(preferences.edit()) doReturn editor

		dataSource.save("пуши")

		verify(editor).putString("search_recent", "пуши")
	}
}
