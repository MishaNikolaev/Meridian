package com.nmichail.meridian.features.conversation.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.conversation.di.ConversationComponentHolder
import com.nmichail.meridian.features.conversation.presentation.compose.ConversationScreen

class ConversationFragment : FullScreenFragment() {

	private val viewModel: ConversationViewModel by viewModels {
		ConversationComponentHolder.get().viewModelFactory()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val chatId = requireArguments().getString(ConversationDestination.CHAT_ID).orEmpty()
		val title = requireArguments().getString(ConversationDestination.TITLE).orEmpty()
		return ComposeView(requireContext()).apply {
			setContent {
				Screen {
					ConversationScreen(
						state = viewModel.state,
						chatId = chatId,
						title = title,
						onAttach = viewModel::attach,
						onBack = viewModel::back,
						onEdit = viewModel::beginEdit,
						onCancelEdit = viewModel::cancelEdit,
						onDraft = viewModel::onDraft,
						onSend = viewModel::send,
					)
				}
			}
		}
	}

	override fun navigateBack() {
		viewModel.back()
	}
}
