package com.nmichail.meridian.features.person.presentation

import com.nmichail.meridian.core.network.error.presentation.BlockingErrorProcessResult
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.features.person.domain.usecase.LoadPersonUseCase
import javax.inject.Inject

class PersonViewModel @Inject constructor(
	private val loadPersonUseCase: LoadPersonUseCase,
	private val errorDelegate: ErrorDelegate,
	private val router: PersonRouter,
) : BaseViewModel<PersonState, PersonIntent, Nothing>() {

	override fun initState(): PersonState = PersonState.Initial

	override fun applyIntent(intent: PersonIntent) {
		when (intent) {
			is PersonIntent.Load     -> handleLoad(intent)
			PersonIntent.Back        -> router.navigateBack()
			PersonIntent.Write       -> router.navigateBack()
			PersonIntent.OpenManager -> handleOpenManager()
		}
	}

	private fun handleOpenManager() {
		val current = state.value as? PersonState.Content ?: return
		if (current.managerId.isEmpty()) {
			return
		}
		router.navigateToPerson(
			userId = current.managerId,
			corporate = true,
		)
	}

	private fun handleLoad(intent: PersonIntent.Load) {
		if (state.value is PersonState.Content || state.value is PersonState.Loading) {
			return
		}

		setState(PersonState.Loading)

		launchTry {
			val card = loadPersonUseCase(intent.userId)
			setState(
				PersonState.Content(
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
				),
			)
		} catch ::handleLoadFailure
	}

	private fun handleLoadFailure(error: Exception) {
		val result = errorDelegate.processBlockingError(
			exception = error,
			retryIntent = PersonIntent.Back,
			backIntent = PersonIntent.Back,
		)

		if (result is BlockingErrorProcessResult.Handled) {
			return
		}

		setState(
			PersonState.Error(
				message = error.userMessage()
			)
		)
	}
}