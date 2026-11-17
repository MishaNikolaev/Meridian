package com.nmichail.meridian.features.profile.about.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.profile.about.di.AboutComponentHolder
import com.nmichail.meridian.features.profile.about.presentation.compose.AboutScreen

fun getAboutScreen(): FullScreen = FullScreen(
	key = AboutFragment::class.java.name,
	fragmentClass = AboutFragment::class.java,
)

class AboutFragment : FullScreenFragment() {

	private val viewModel: AboutViewModel by viewModels {
		AboutComponentHolder.get().viewModelFactory()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				Screen {
					AboutScreen(onIntent = viewModel::applyIntent)
				}
			}
		}

	override fun navigateBack() {
		viewModel.applyIntent(AboutIntent.Back)
	}
}