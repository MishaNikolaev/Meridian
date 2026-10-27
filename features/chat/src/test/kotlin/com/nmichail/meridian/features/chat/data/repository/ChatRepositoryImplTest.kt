package com.nmichail.meridian.features.chat.data.repository

import com.nmichail.meridian.features.chat.data.datasource.ChatLocalDataSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ChatRepositoryImplTest {

	private val dataSource: ChatLocalDataSource = mock()
	private val repository = ChatRepositoryImpl(dataSource)

	@Test
	fun `get text EXPECT stored text`() {
		whenever(dataSource.getText()) doReturn "текст"

		val actual = repository.getText()

		assertEquals("текст", actual)
	}

	@Test
	fun `save EXPECT data source save`() {
		repository.save("текст")

		verify(dataSource).save("текст")
	}
}