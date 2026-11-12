package com.nmichail.meridian.features.person.presentation

import com.nmichail.meridian.component.photopicker.PhotoLocalDataSource
import com.nmichail.meridian.core.network.error.presentation.BlockingErrorProcessResult
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.features.person.domain.usecase.LoadPersonUseCase
import com.nmichail.meridian.features.person.domain.usecase.ReadPersonUseCase
import javax.inject.Inject

class PersonViewModel @Inject constructor(
	private val loadPersonUseCase: LoadPersonUseCase,
	private val readPersonUseCase: ReadPersonUseCase,
	private val errorDelegate: ErrorDelegate,
	private val router: PersonRouter,
) : BaseViewModel<PersonState, PersonIntent, Nothing>() {

	override fun initState(): PersonState = PersonState.Initial

	override fun applyIntent(intent: PersonIntent) {
		when (intent) {
			is PersonIntent.Load        -> handleLoad(intent)
			is PersonIntent.Back        -> router.navigateBack()
			is PersonIntent.Write       -> router.navigateBack()
			is PersonIntent.OpenManager -> handleOpenManager()
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

		launchTry {
			val cached = readPersonUseCase(intent.userId)

			if (cached == null) {
				setState(PersonState.Loading)
			} else {
				PhotoLocalDataSource.notePhoto(userId = intent.userId, photoId = cached.photo)

				setState(
					PersonState.Content(
						name = cached.name,
						position = cached.position,
						department = cached.department,
						office = cached.office,
						server = cached.server,
						status = cached.status,
						manager = cached.manager,
						managerId = cached.managerId,
						email = cached.email,
						phone = cached.phone,
						photo = cached.photo,
						corporate = intent.corporate,
					),
				)
			}

			val card = loadPersonUseCase(intent.userId)

			PhotoLocalDataSource.notePhoto(userId = intent.userId, photoId = card.photo)

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
					photo = card.photo,
					corporate = intent.corporate,
				),
			)
		} catch ::handleOpenFailure
	}

	private fun handleOpenFailure(error: Exception) {
		if (state.value is PersonState.Content) {
			errorDelegate.processBlockingError(
				exception = error,
				retryIntent = PersonIntent.Back,
				backIntent = PersonIntent.Back,
			)

			return
		}

		handleLoadFailure(error)
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