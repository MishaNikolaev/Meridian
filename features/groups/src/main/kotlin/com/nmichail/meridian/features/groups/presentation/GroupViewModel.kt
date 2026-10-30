package com.nmichail.meridian.features.groups.presentation

import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.presentation.NonBlockingErrorProcessResult
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.core.validation.displayName.ValidateDisplayNameUseCase
import com.nmichail.meridian.core.validation.displayName.isValid
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.core.validation.username.ValidateUsernameUseCase
import com.nmichail.meridian.core.validation.username.isValid
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.usecase.CreateChatUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.FindPersonUseCase
import javax.inject.Inject

class GroupViewModel @Inject constructor(
	private val findPersonUseCase: FindPersonUseCase,
	private val createChatUseCase: CreateChatUseCase,
	private val validateUsernameUseCase: ValidateUsernameUseCase,
	private val validateDisplayNameUseCase: ValidateDisplayNameUseCase,
	private val errorDelegate: ErrorDelegate,
	private val router: GroupRouter,
) : BaseViewModel<GroupState, GroupIntent, Nothing>() {

	override fun initState(): GroupState = GroupState.Initial

	override fun applyIntent(intent: GroupIntent) {
		when (intent) {
			is GroupIntent.Load -> handleLoad()
			is GroupIntent.ChangeTitle -> handleChangeTitle(intent.title)
			is GroupIntent.ChangeDescription -> handleChangeDescription(intent.description)
			is GroupIntent.ChangeLogin -> handleChangeLogin(intent.login)
			is GroupIntent.OpenMembers -> handleOpenMembers()
			is GroupIntent.AddMember -> handleAddMember()
			is GroupIntent.RemoveMember -> handleRemoveMember(intent.userId)
			is GroupIntent.Submit -> handleSubmit()
			is GroupIntent.NavigateBack -> router.navigateBack()
		}
	}

	private fun handleLoad() {
		if (state.value != GroupState.Initial) {
			return
		}
		setState(
			GroupState.Content(
				title = DisplayNameValidationItem(),
				description = "",
				login = UsernameValidationItem(),
				adding = false,
				members = emptyList(),
			),
		)
	}

	private fun handleChangeTitle(title: String) {
		when (val current = state.value) {
			GroupState.Initial,
			is GroupState.Loading,
			-> return

			is GroupState.Content -> setState(
				current.copy(title = validateDisplayNameUseCase(title)),
			)

			is GroupState.Error -> setState(
				GroupState.Content(
					title = validateDisplayNameUseCase(title),
					description = current.description,
					login = current.login,
					adding = current.adding,
					members = current.members,
				),
			)
		}
	}

	private fun handleChangeDescription(description: String) {
		when (val current = state.value) {
			GroupState.Initial,
			is GroupState.Loading,
			-> return

			is GroupState.Content -> setState(current.copy(description = description))

			is GroupState.Error -> setState(
				GroupState.Content(
					title = current.title,
					description = description,
					login = current.login,
					adding = current.adding,
					members = current.members,
				),
			)
		}
	}

	private fun handleChangeLogin(login: String) {
		when (val current = state.value) {
			GroupState.Initial,
			is GroupState.Loading,
			-> return

			is GroupState.Content -> setState(
				current.copy(login = validateUsernameUseCase(login)),
			)

			is GroupState.Error -> setState(
				GroupState.Content(
					title = current.title,
					description = current.description,
					login = validateUsernameUseCase(login),
					adding = current.adding,
					members = current.members,
				),
			)
		}
	}

	private fun handleOpenMembers() {
		when (val current = state.value) {
			GroupState.Initial,
			is GroupState.Loading,
			-> return

			is GroupState.Content -> setState(current.copy(adding = true))

			is GroupState.Error -> setState(
				GroupState.Content(
					title = current.title,
					description = current.description,
					login = current.login,
					adding = true,
					members = current.members,
				),
			)
		}
	}

	private fun handleRemoveMember(userId: String) {
		when (val current = state.value) {
			GroupState.Initial,
			is GroupState.Loading,
			-> return

			is GroupState.Content -> setState(
				current.copy(members = current.members.filter { person -> person.userId != userId }),
			)

			is GroupState.Error -> setState(
				GroupState.Content(
					title = current.title,
					description = current.description,
					login = current.login,
					adding = current.adding,
					members = current.members.filter { person -> person.userId != userId },
				),
			)
		}
	}

	private fun handleAddMember() {
		when (val current = state.value) {
			GroupState.Initial,
			is GroupState.Loading,
			-> return

			is GroupState.Content -> handleFindPerson(
				title = current.title,
				description = current.description,
				login = current.login,
				adding = current.adding,
				members = current.members,
			)

			is GroupState.Error -> handleFindPerson(
				title = current.title,
				description = current.description,
				login = current.login,
				adding = current.adding,
				members = current.members,
			)
		}
	}

	private fun handleFindPerson(
		title: DisplayNameValidationItem,
		description: String,
		login: UsernameValidationItem,
		adding: Boolean,
		members: List<ChatPerson>,
	) {
		val checked = validateUsernameUseCase(login.data.trim())
		if (!checked.isValid()) {
			setState(
				GroupState.Content(
					title = title,
					description = description,
					login = checked,
					adding = adding,
					members = members,
				),
			)
			return
		}
		if (members.any { person -> person.username.equals(checked.data, ignoreCase = true) }) {
			setState(
				GroupState.Content(
					title = title,
					description = description,
					login = UsernameValidationItem(),
					adding = false,
					members = members,
				),
			)
			return
		}
		setState(
			GroupState.Loading(
				title = title,
				description = description,
				login = checked,
				adding = adding,
				members = members,
			),
		)
		loadPerson(checked.data)
	}

	private fun loadPerson(username: String) {
		launchTry {
			val person = findPersonUseCase(username)
			val opened = state.value as? GroupState.Loading ?: return@launchTry
			setState(
				GroupState.Content(
					title = opened.title,
					description = opened.description,
					login = UsernameValidationItem(),
					adding = false,
					members = opened.members + person,
				),
			)
		} catch ::handleAddFailure
	}

	private fun handleSubmit() {
		when (val current = state.value) {
			GroupState.Initial,
			is GroupState.Loading,
			-> return

			is GroupState.Content -> handleCreateChat(
				title = current.title,
				description = current.description,
				login = current.login,
				adding = current.adding,
				members = current.members,
			)

			is GroupState.Error -> handleCreateChat(
				title = current.title,
				description = current.description,
				login = current.login,
				adding = current.adding,
				members = current.members,
			)
		}
	}

	private fun handleCreateChat(
		title: DisplayNameValidationItem,
		description: String,
		login: UsernameValidationItem,
		adding: Boolean,
		members: List<ChatPerson>,
	) {
		val checked = validateDisplayNameUseCase(title.data.trim())
		if (!checked.isValid()) {
			setState(
				GroupState.Content(
					title = checked,
					description = description,
					login = login,
					adding = adding,
					members = members,
				),
			)
			return
		}

		setState(
			GroupState.Loading(
				title = checked,
				description = description,
				login = login,
				adding = adding,
				members = members,
			),
		)

		launchTry {
			val chat = createChatUseCase(
				ChatType.GROUP,
				checked.data,
				members.map { person -> person.username },
			)

			router.navigateBack()

			router.navigateToConversation(
				chatId = chat.id,
				title = chat.title,
				corporate = false,
				personId = "",
			)
		} catch ::handleCreateFailure
	}

	private fun handleAddFailure(error: Exception) {
		handleRequestFailure(error)
	}

	private fun handleCreateFailure(error: Exception) {
		handleRequestFailure(error)
	}

	private fun handleRequestFailure(error: Exception) {
		val failed = state.value as? GroupState.Loading ?: return
		val result = errorDelegate.processNonBlockingError(error)

		if (result is NonBlockingErrorProcessResult.Handled) {
			setState(
				GroupState.Content(
					title = failed.title,
					description = failed.description,
					login = failed.login,
					adding = failed.adding,
					members = failed.members,
				),
			)
			return
		}

		setState(
			GroupState.Error(
				title = failed.title,
				description = failed.description,
				login = failed.login,
				adding = failed.adding,
				members = failed.members,
				message = error.userMessage(),
			),
		)
	}
}