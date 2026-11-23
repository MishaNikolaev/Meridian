package com.nmichail.meridian.features.person.presentation

import com.nmichail.meridian.component.photopicker.domain.usecase.KeepPersonPhotoUseCase
import com.nmichail.meridian.core.validation.capitalize.CapitalizeUseCase
import com.nmichail.meridian.core.network.error.presentation.BlockingErrorProcessResult
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.features.person.domain.usecase.LoadPersonUseCase
import com.nmichail.meridian.features.person.domain.usecase.ReadPersonUseCase
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.usecase.CreateChatUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadChatsUseCase
import com.nmichail.meridian.shared.session.domain.usecase.GetUserIdUseCase
import javax.inject.Inject

class PersonViewModel @Inject constructor(
	private val loadPersonUseCase: LoadPersonUseCase,
	private val readPersonUseCase: ReadPersonUseCase,
	private val keepPersonPhotoUseCase: KeepPersonPhotoUseCase,
	private val createChatUseCase: CreateChatUseCase,
	private val readChatsUseCase: ReadChatsUseCase,
	private val errorDelegate: ErrorDelegate,
	private val router: PersonRouter,
	private val getUserIdUseCase: GetUserIdUseCase,
	private val capitalizeUseCase: CapitalizeUseCase,
) : BaseViewModel<PersonState, PersonIntent, PersonEvent>() {

	override fun initState(): PersonState = PersonState.Initial

	override fun applyIntent(intent: PersonIntent) {
		when (intent) {
			is PersonIntent.Load        -> handleLoad(intent)
			is PersonIntent.Back        -> handleBack()
			is PersonIntent.Write       -> handleOpen(intent.userId, video = null)
			is PersonIntent.Call        -> handleOpen(intent.userId, video = false)
			is PersonIntent.Video       -> handleOpen(intent.userId, video = true)
			is PersonIntent.OpenManager -> handleOpenManager()
		}
	}

	private fun handleBack() {
		router.navigateBack()
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

	private fun handleOpen(userId: String, video: Boolean?) {
		val current = state.value as? PersonState.Content ?: return

		if (userId.isEmpty()) {
			return
		}

		val currentUserId = getUserIdUseCase().orEmpty()
		if (video != null && currentUserId.isNotEmpty() && userId == currentUserId) {
			setEvent(PersonEvent.CannotCallSelf)
			return
		}

		launchTry {
			val directChat = readChatsUseCase()?.firstOrNull { chat ->
				chat.personId == userId && chat.type == ChatType.DIRECT
			}

			if (directChat != null) {
				navigateToPersonalChat(
					directChat.id,
					directChat.title.ifBlank { current.name },
					directChat.corporate, userId, video
				)

				return@launchTry
			}

			val chat = createChatUseCase(ChatType.DIRECT, "", listOf(userId))

			navigateToPersonalChat(chat.id, chat.title.ifBlank { current.name }, chat.corporate, userId, video)
		} catch ::handleOpenFailure
	}

	private fun navigateToPersonalChat(chatId: String, title: String, corporate: Boolean, userId: String, video: Boolean?) {
		if (video == null) {
			router.navigateToConversation(
				chatId = chatId,
				title = title,
				corporate = corporate,
				personId = userId,
			)
			return
		}

		router.navigateToCall(
			chatId = chatId,
			title = title,
			personId = userId,
			video = video,
		)
	}

	private fun handleLoad(intent: PersonIntent.Load) {
		val current = state.value

		if (current is PersonState.Loading) {
			return
		}

		if (current is PersonState.Content && current.userId == intent.userId) {
			return
		}

		launchTry {
			val person = readPersonUseCase(intent.userId)

			if (person == null) {
				setState(PersonState.Loading)
			} else {
				keepPersonPhotoUseCase(intent.userId, person.photo)

				setState(
					PersonState.Content(
						userId = intent.userId,
						name = person.name,
						position = capitalizeUseCase(person.position),
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
					userId = intent.userId,
					name = card.name,
					position = capitalizeUseCase(card.position),
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