package com.nmichail.meridian.features.profile.about.presentation

import com.nmichail.meridian.core.presentation.BaseViewModel
import javax.inject.Inject

class AboutViewModel @Inject constructor(
	private val router: AboutRouter,
) : BaseViewModel<Unit, AboutIntent, Nothing>() {

	override fun initState(): Unit = Unit

	override fun applyIntent(intent: AboutIntent) {
		when (intent) {
			AboutIntent.Back -> router.navigateBack()
			is AboutIntent.OpenRepository -> router.openLink(intent.url)
		}
	}
}