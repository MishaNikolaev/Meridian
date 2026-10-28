package com.nmichail.meridian.features.conversation.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.viewModels
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.conversation.di.ConversationComponentHolder
import com.nmichail.meridian.features.conversation.presentation.compose.ConversationScreen

fun getConversationScreen(
	chatId: String,
	title: String,
	corporate: Boolean,
	messageId: String? = null,
	personId: String = "",
): FullScreen = object : FullScreen(
	key = listOfNotNull("conversation", chatId, messageId).joinToString("-"),
	fragmentClass = ConversationFragment::class.java,
) {
	override fun createFragment(factory: FragmentFactory): Fragment = ConversationFragment().apply {
		arguments = Bundle().apply {
			putString("chatId", chatId)
			putString("title", title)
			putBoolean("corporate", corporate)
			putString("messageId", messageId)
			putString("personId", personId)
		}
	}
}

class ConversationFragment : FullScreenFragment() {

	private val viewModel: ConversationViewModel by viewModels {
		ConversationComponentHolder.get().viewModelFactory()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				Screen {
					val arguments = requireArguments()
					ConversationScreen(
						chatId = arguments.getString("chatId").orEmpty(),
						title = arguments.getString("title").orEmpty(),
						corporate = arguments.getBoolean("corporate"),
						rootId = arguments.getString("messageId"),
						personId = arguments.getString("personId").orEmpty(),
						state = viewModel.state,
						onIntent = viewModel::applyIntent,
					)
				}
			}
		}

	override fun navigateBack() {
		viewModel.applyIntent(ConversationIntent.NavigateBack)
	}
}