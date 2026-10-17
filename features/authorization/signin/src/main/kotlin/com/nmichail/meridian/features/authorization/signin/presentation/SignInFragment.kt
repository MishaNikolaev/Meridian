package com.nmichail.meridian.features.authorization.signin.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import com.nmichail.meridian.component.ui.theme.MeridianTheme
import com.nmichail.meridian.core.navigation.screen.FullScreen
import com.nmichail.meridian.core.navigation.screen.FullScreenFragment
import com.nmichail.meridian.features.authorization.signin.di.SignInComponentHolder
import com.nmichail.meridian.features.authorization.signin.presentation.compose.SignInScreen
import javax.inject.Inject

fun getSignInScreen(): FullScreen = FullScreen(
	key = SignInFragment::class.java.name,
	fragmentClass = SignInFragment::class.java,
)

class SignInFragment : FullScreenFragment() {

	@Inject
	lateinit var router: SignInRouter

	private val viewModel: SignInViewModel by viewModel()

	override fun onAttach(context: Context) {
		super.onAttach(context)
		SignInComponentHolder.get().inject(this)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		ComposeView(requireContext()).apply {
			setContent {
				MeridianTheme {
					SignInScreen(viewModel)
				}
			}
		}

	override fun navigateBack() {
		router.navigateBack()
	}
}
