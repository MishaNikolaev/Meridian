package com.nmichail.meridian.features.authorization.splash.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.nmichail.meridian.component.ui.theme.MeridianTheme
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.authorization.splash.di.SplashComponentHolder
import com.nmichail.meridian.features.authorization.splash.presentation.compose.SplashScreen

fun getSplashScreen(): FullScreen = FullScreen(
	key = SplashFragment::class.java.name,
	fragmentClass = SplashFragment::class.java,
)

class SplashFragment : FullScreenFragment() {

	private val viewModel: SplashViewModel by viewModel()

	override fun onAttach(context: Context) {
		super.onAttach(context)
		SplashComponentHolder.get().inject(this)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				MeridianTheme {
					SplashScreen(onIntent = viewModel::applyIntent)
				}
			}
		}

	override fun navigateBack() = Unit
}