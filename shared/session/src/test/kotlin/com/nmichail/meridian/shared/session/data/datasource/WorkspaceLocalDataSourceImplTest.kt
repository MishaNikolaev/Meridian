package com.nmichail.meridian.shared.session.data.datasource

import android.content.Context
import android.content.SharedPreferences
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class WorkspaceLocalDataSourceImplTest {

	private val context: Context = mock()
	private val preferences: SharedPreferences = mock()
	private val editor: SharedPreferences.Editor = mock()
	private lateinit var dataSource: WorkspaceLocalDataSourceImpl

	@BeforeEach
	fun setUp() {
		whenever(context.getSharedPreferences(any(), any())) doReturn preferences
		whenever(preferences.edit()) doReturn editor
		whenever(editor.putString(any(), any())) doReturn editor
		whenever(editor.remove(any())) doReturn editor
		dataSource = WorkspaceLocalDataSourceImpl(context)
	}

	@Test
	fun `save host EXPECT stored host`() {
		dataSource.saveHost(" meridian.name.ru ")

		verify(editor).putString("session_host", "meridian.name.ru")
	}

	@Test
	fun `clear EXPECT removed host`() {
		dataSource.clearWorkspace()

		verify(editor).remove("session_host")
	}
}