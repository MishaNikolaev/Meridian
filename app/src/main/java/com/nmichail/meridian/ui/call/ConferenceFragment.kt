package com.nmichail.meridian.ui.call

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.calls.presentation.compose.conference.ConferenceRoom

fun getConferenceScreen(code: String, onBack: () -> Unit): FullScreen = object : FullScreen(
	key = "conference-$code",
	fragmentClass = ConferenceFragment::class.java,
) {
	override fun createFragment(factory: FragmentFactory): Fragment = ConferenceFragment().apply {
		arguments = Bundle().apply {
			putString("code", code)
		}
		back = onBack
	}
}

class ConferenceFragment : FullScreenFragment() {
	var back: (() -> Unit)? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				Screen {
					ConferenceRoom(
						title = requireArguments().getString("code").orEmpty(),
						onClose = { navigateBack() },
					)
				}
			}
		}

	override fun navigateBack() {
		back?.invoke()
	}
}
