package com.nmichail.meridian.ui.host

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.nmichail.meridian.component.ui.bar.model.MUiBottomDestination
import com.nmichail.meridian.component.ui.theme.MeridianTheme
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.chat.di.ChatComponentHolder
import com.nmichail.meridian.features.chat.presentation.ChatViewModel
import com.nmichail.meridian.features.profile.di.ProfileComponentHolder
import com.nmichail.meridian.features.profile.presentation.ProfileViewModel
import com.nmichail.meridian.features.mainhost.presentation.MainHostRouter
import com.nmichail.meridian.navigation.router.host.MainHostRouterHolder

fun getMainHostScreen(): FullScreen = FullScreen(
	key = MainHostFragment::class.java.name,
	fragmentClass = MainHostFragment::class.java,
)

class MainHostFragment : FullScreenFragment() {

	private companion object {

		const val TAB = "tab"
	}

	private lateinit var router: MainHostRouter

	private val selected = mutableStateOf(MUiBottomDestination.HOME)

	private val viewModel: ProfileViewModel by viewModels {
		ProfileComponentHolder.get().viewModelFactory()
	}

	private val chatViewModel: ChatViewModel by viewModels {
		ChatComponentHolder.get().viewModelFactory()
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		router = MainHostRouterHolder.get()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val restored = savedInstanceState?.getString(TAB) ?: return
		selected.value = MUiBottomDestination.valueOf(restored)
	}

	override fun onSaveInstanceState(outState: Bundle) {
		outState.putString(TAB, selected.value.name)
		super.onSaveInstanceState(outState)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				MeridianTheme {
					MainHostScreen(
						selected = selected.value,
						onSelect = { selected.value = it },
						chatState = chatViewModel.state,
						onChatIntent = chatViewModel::applyIntent,
						chatEvent = chatViewModel.event,
						profileState = viewModel.state,
						onProfileLoad = viewModel::load,
						onLogout = viewModel::logout,
					)
				}
			}
		}

	override fun navigateBack() {
		if (selected.value == MUiBottomDestination.HOME) {
			router.navigateBack()
		} else {
			selected.value = MUiBottomDestination.HOME
		}
	}
}