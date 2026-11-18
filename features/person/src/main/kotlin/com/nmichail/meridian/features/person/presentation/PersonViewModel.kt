package com.nmichail.meridian.features.person.presentation

import com.nmichail.meridian.component.photopicker.domain.usecase.KeepPersonPhotoUseCase
import com.nmichail.meridian.core.network.error.presentation.BlockingErrorProcessResult
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.features.person.domain.usecase.LoadPersonUseCase
import com.nmichail.meridian.features.person.domain.usecase.ReadPersonUseCase
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.usecase.CreateChatUseCase
import javax.inject.Inject

class PersonViewModel @Inject constructor(
	private val loadPersonUseCase: LoadPersonUseCase,
	private val readPersonUseCase: ReadPersonUseCase,
	private val keepPersonPhotoUseCase: KeepPersonPhotoUseCase,
	private val createChatUseCase: CreateChatUseCase,
	private val errorDelegate: ErrorDelegate,
	private val router: PersonRouter,
) : BaseViewModel<PersonState, PersonIntent, Nothing>() {
	private var openedId = ""

	override fun initState(): PersonState = PersonState.Initial

	override fun applyIntent(intent: PersonIntent) {
		when (intent) {
			is PersonIntent.Load        -> handleLoad(intent)
			is PersonIntent.Back        -> router.navigateBack()
			is PersonIntent.Write       -> handleWrite()
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

	private fun handleWrite() {
		val current = state.value as? PersonState.Content ?: return

		if (openedId.isEmpty()) {
			return
		}

		launchTry {
			val chat = createChatUseCase(ChatType.DIRECT, "", listOf(openedId))

			router.navigateToConversation(
				chatId = chat.id,
				title = chat.title.ifBlank { current.name },
				corporate = chat.corporate,
				personId = openedId,
			)
		} catch ::handleOpenFailure
	}

	private fun handleLoad(intent: PersonIntent.Load) {
		if (state.value is PersonState.Loading) {
			return
		}

		if (intent.userId == openedId && state.value is PersonState.Content) {
			return
		}

		openedId = intent.userId

		launchTry {
			val person = readPersonUseCase(intent.userId)

			if (person == null) {
				setState(PersonState.Loading)
			} else {
				keepPersonPhotoUseCase(intent.userId, person.photo)

				setState(
					PersonState.Content(
						name = person.name,
						position = person.position,
						department = person.department,
						office = person.office,
						server = person.server,
						status = person.status,
						manager = person.manager,
						managerId = person.managerId,
						email = person.email,
						phone = person.phone,
						photo = person.photo,
						corporate = intent.corporate,
					),
				)
			}

			val card = loadPersonUseCase(intent.userId)

			keepPersonPhotoUseCase(intent.userId, card.photo)

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