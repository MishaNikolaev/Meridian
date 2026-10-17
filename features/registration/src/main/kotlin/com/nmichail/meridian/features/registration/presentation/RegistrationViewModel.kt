package com.nmichail.meridian.features.registration.presentation

import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.nmichail.meridian.core.presentation.ui.base.BaseViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

class RegistrationViewModel @Inject constructor(
	storeFactory: RegistrationStoreFactory,
	private val router: RegistrationRouter,
) : BaseViewModel() {

	override val store: RegistrationStore = storeFactory.create()

	init {
		viewModelScope.launch {
			store.labels.collect { label ->
				when (label) {
					RegistrationStore.Label.OpenHome -> router.openHome()
					RegistrationStore.Label.OpenSignIn -> router.openSignIn()
					RegistrationStore.Label.OpenWorkspace -> router.openWorkspace()
				}
			}
		}
	}
}
