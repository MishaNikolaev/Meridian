package com.nmichail.meridian.navigation.router.search

import com.nmichail.meridian.core.navigation.router.GlobalRouter
import com.nmichail.meridian.features.search.presentation.SearchRouter
import javax.inject.Inject

class SearchRouterImpl @Inject constructor(
	private val router: GlobalRouter,
) : SearchRouter {

	override fun navigateBack() {
		router.navigateBack()
	}
}