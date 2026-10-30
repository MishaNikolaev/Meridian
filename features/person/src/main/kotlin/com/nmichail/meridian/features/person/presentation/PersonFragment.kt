package com.nmichail.meridian.features.person.presentation

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
import com.nmichail.meridian.features.person.di.PersonComponentHolder
import com.nmichail.meridian.features.person.presentation.compose.PersonScreen

fun getPersonScreen(userId: String, corporate: Boolean): FullScreen = object : FullScreen(
	key = "person-$userId",
	fragmentClass = PersonFragment::class.java,
) {
	override fun createFragment(factory: FragmentFactory): Fragment = PersonFragment().apply {
		arguments = Bundle().apply {
			putString("userId", userId)
			putBoolean("corporate", corporate)
		}
	}
}

class PersonFragment : FullScreenFragment() {

	private val viewModel: PersonViewModel by viewModels {
		PersonComponentHolder.get().viewModelFactory()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				Screen {
					val arguments = requireArguments()
					PersonScreen(
						userId = arguments.getString("userId").orEmpty(),
						corporate = arguments.getBoolean("corporate"),
						state = viewModel.state,
						onIntent = viewModel::applyIntent,
					)
				}
			}
		}

	override fun navigateBack() {
		viewModel.applyIntent(PersonIntent.Back)
	}
}