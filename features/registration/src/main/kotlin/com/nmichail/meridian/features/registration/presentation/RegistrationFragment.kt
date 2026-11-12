package com.nmichail.meridian.features.registration.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.registration.di.RegistrationComponentHolder
import com.nmichail.meridian.features.registration.presentation.compose.RegistrationScreen

fun getRegistrationScreen(): FullScreen = FullScreen(
	key = RegistrationFragment::class.java.name,
	fragmentClass = RegistrationFragment::class.java,
)

class RegistrationFragment : FullScreenFragment() {
	private val viewModel: RegistrationViewModel by viewModel()

	override fun onAttach(context: Context) {
		super.onAttach(context)

		RegistrationComponentHolder.get().inject(this)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				Screen(canvas = true) {
					RegistrationScreen(
						state = viewModel.state,
						onIntent = viewModel::applyIntent,
					)
				}
			}
		}

	override fun navigateBack() {
		viewModel.applyIntent(RegistrationIntent.NavigateBack)
	}
}
