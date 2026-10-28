package com.nmichail.meridian.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.launchTry
import com.nmichail.meridian.features.profile.domain.usecase.LoadPersonUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PersonViewModel @Inject constructor(
	private val loadPersonUseCase: LoadPersonUseCase,
	private val router: ProfileRouter,
) : ViewModel() {

	private val stateFlow = MutableStateFlow<PersonState>(PersonState.Initial)
	val state: StateFlow<PersonState> = stateFlow.asStateFlow()

	fun applyIntent(intent: PersonIntent) {
		when (intent) {
			is PersonIntent.Load -> load(intent)
			PersonIntent.Back -> router.navigateBack()
			PersonIntent.Write -> router.navigateBack()
			PersonIntent.OpenManager -> openManager()
		}
	}

	private fun openManager() {
		val current = stateFlow.value as? PersonState.Content ?: return
		if (current.managerId.isEmpty()) {
			return
		}
		router.navigateToPerson(
			userId = current.managerId,
			corporate = true,
		)
	}

	private fun load(intent: PersonIntent.Load) {
		if (stateFlow.value is PersonState.Content || stateFlow.value is PersonState.Loading) {
			return
		}
		stateFlow.value = PersonState.Loading
		viewModelScope.launchTry {
			val card = loadPersonUseCase(intent.userId)
			stateFlow.value = PersonState.Content(
				name = card.name,
				position = card.position,
				department = card.department,
				office = card.office,
				server = card.server,
				status = card.status,
				manager = card.manager,
				managerId = card.managerId,
				email = card.email,
				phone = card.phone,
				corporate = intent.corporate,
			)
		} catch ::handleError
	}

	private fun handleError(error: Exception) {
		stateFlow.value = PersonState.Error(message = error.userMessage())
	}
}
