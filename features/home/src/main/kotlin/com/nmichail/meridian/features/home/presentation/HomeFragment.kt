package com.nmichail.meridian.features.home.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.nmichail.meridian.component.ui.theme.MeridianTheme
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.home.di.HomeComponentHolder
import com.nmichail.meridian.features.home.presentation.compose.HomeScreen
import javax.inject.Inject

fun getHomeScreen(): FullScreen = FullScreen(
	key = HomeFragment::class.java.name,
	fragmentClass = HomeFragment::class.java,
)

class HomeFragment : FullScreenFragment() {

	@Inject
	lateinit var router: HomeRouter

	override fun onAttach(context: Context) {
		super.onAttach(context)
		HomeComponentHolder.get().inject(this)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				MeridianTheme {
					HomeScreen()
				}
			}
		}

	override fun navigateBack() {
		router.navigateBack()
	}
}