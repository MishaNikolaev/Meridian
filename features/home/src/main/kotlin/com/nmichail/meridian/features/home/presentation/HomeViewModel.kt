package com.nmichail.meridian.features.home.presentation

import com.nmichail.meridian.core.presentation.ui.base.BaseViewModel
import javax.inject.Inject

class HomeViewModel @Inject constructor(
	storeFactory: HomeStoreFactory,
) : BaseViewModel() {

	override val store: HomeStore = storeFactory.create()
}
