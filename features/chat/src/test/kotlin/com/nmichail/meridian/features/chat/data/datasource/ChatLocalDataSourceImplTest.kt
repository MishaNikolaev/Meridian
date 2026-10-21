package com.nmichail.meridian.features.chat.data.datasource

import com.nmichail.meridian.core.storage.LocalPreferences
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ChatLocalDataSourceImplTest {

	private val localPreferences: LocalPreferences = mock()
	private val dataSource = ChatLocalDataSourceImpl(localPreferences)

	@Test
	fun `stored text EXPECT text`() {
		whenever(localPreferences.getString("home_text")) doReturn "черновик"

		val actual = dataSource.getText()

		assertEquals("черновик", actual)
	}

	@Test
	fun `missing text EXPECT empty text`() {
		whenever(localPreferences.getString("home_text")) doReturn null

		val actual = dataSource.getText()

		assertEquals("", actual)
	}

	@Test
	fun `save EXPECT stored text`() {
		dataSource.save("черновик")

		verify(localPreferences).putString("home_text", "черновик")
	}
}
