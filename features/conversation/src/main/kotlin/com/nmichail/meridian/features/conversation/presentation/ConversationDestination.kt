package com.nmichail.meridian.features.conversation.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.nmichail.meridian.core.navigation.screen.FullScreen

class ConversationDestination(
	private val chatId: String,
	private val title: String,
) : FullScreen(
	key = "conversation-$chatId",
	fragmentClass = ConversationFragment::class.java,
) {

	companion object {
		const val CHAT_ID = "chatId"
		const val TITLE = "title"
	}

	override fun createFragment(factory: FragmentFactory): Fragment =
		ConversationFragment().apply {
			arguments = Bundle().apply {
				putString(CHAT_ID, chatId)
				putString(TITLE, title)
			}
		}
}
