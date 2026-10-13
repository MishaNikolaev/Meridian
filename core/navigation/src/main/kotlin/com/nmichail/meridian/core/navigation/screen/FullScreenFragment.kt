package com.nmichail.meridian.core.navigation.screen

import androidx.activity.addCallback
import com.nmichail.meridian.core.presentation.ui.base.BaseFragment

abstract class FullScreenFragment : BaseFragment() {

	abstract fun navigateBack()

	override fun onStart() {
		requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
			navigateBack()
		}
		super.onStart()
	}
}
