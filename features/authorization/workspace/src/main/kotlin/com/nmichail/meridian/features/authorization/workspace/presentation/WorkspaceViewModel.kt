package com.nmichail.meridian.features.authorization.workspace.presentation

import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.nmichail.meridian.core.presentation.ui.base.BaseViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

class WorkspaceViewModel @Inject constructor(
	storeFactory: WorkspaceStoreFactory,
	private val router: WorkspaceRouter,
) : BaseViewModel() {

	override val store: WorkspaceStore = storeFactory.create()

	init {
		viewModelScope.launch {
			store.labels.collect { label ->
				when (label) {
					WorkspaceStore.Label.OpenHome -> router.openHome()
					WorkspaceStore.Label.OpenSignIn -> router.openSignIn()
				}
			}
		}
	}
}
