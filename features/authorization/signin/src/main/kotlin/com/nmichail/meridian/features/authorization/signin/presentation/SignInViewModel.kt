package com.nmichail.meridian.features.authorization.signin.presentation

import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.nmichail.meridian.core.presentation.ui.base.BaseViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

class SignInViewModel @Inject constructor(
	storeFactory: SignInStoreFactory,
	private val router: SignInRouter,
) : BaseViewModel() {

	override val store: SignInStore = storeFactory.create()

	init {
		viewModelScope.launch {
			store.labels.collect { label ->
				when (label) {
					SignInStore.Label.OpenHome -> router.openHome()
					SignInStore.Label.OpenRegistration -> router.openRegistration()
					SignInStore.Label.OpenWorkspace -> router.openWorkspace()
				}
			}
		}
	}
}
