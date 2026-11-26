package com.nmichail.meridian.features.groups.presentation

import com.nmichail.meridian.component.photopicker.PhotoLocalDataSource
import com.nmichail.meridian.component.photopicker.domain.usecase.UploadPhotoUseCase
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
import com.nmichail.meridian.shared.group.domain.usecase.UpdateChatUseCase
import javax.inject.Inject

internal const val GROUP_EDIT = "group-edit"

class GroupViewModel @Inject constructor(
	private val findPersonUseCase: FindPersonUseCase,
	private val createChatUseCase: CreateChatUseCase,
	private val updateChatUseCase: UpdateChatUseCase,
	private val uploadPhotoUseCase: UploadPhotoUseCase,
	private val validateUsernameUseCase: ValidateUsernameUseCase,
	private val validateDisplayNameUseCase: ValidateDisplayNameUseCase,
	private val errorDelegate: ErrorDelegate,
	private val router: GroupRouter,
) : BaseViewModel<GroupState, GroupIntent, GroupEvent>() {

	override fun initState(): GroupState = GroupState.Initial

	override fun applyIntent(intent: GroupIntent) {
		when (intent) {
			is GroupIntent.Load             -> handleLoad()
			is GroupIntent.Open             -> handleOpen(intent.chatId, intent.title, intent.description)
			is GroupIntent.ChangeTitle      -> handleChangeTitle(intent.title)
			is GroupIntent.ChangeDescription -> handleChangeDescription(intent.description)
			is GroupIntent.ChangeLogin      -> handleChangeLogin(intent.login)
			is GroupIntent.OpenMembers      -> handleOpenMembers()
			is GroupIntent.AddMember        -> handleAddMember()
			is GroupIntent.RemoveMember     -> handleRemoveMember(intent.userId)
			is GroupIntent.Submit           -> handleSubmit()
			is GroupIntent.InstallPhoto     -> handleInstall(intent.bytes, intent.type)
			is GroupIntent.NavigateBack     -> router.navigateBack()
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
				photo = "",
				editing = false,
				chatId = "",
				uploading = false,
			),
		)
	}

	private fun handleOpen(chatId: String, title: String, description: String) {
		if (state.value != GroupState.Initial) {
			return
		}

		val stored = PhotoLocalDataSource.storedChatPhoto(chatId)

		setState(
			GroupState.Content(
				title = validateDisplayNameUseCase(title),
				description = description,
				login = UsernameValidationItem(),
				adding = false,
				members = emptyList(),
				photo = stored,
				editing = true,
				chatId = chatId,
				uploading = false,
			),
		)
	}

	private fun handleInstall(bytes: ByteArray, type: String) {
		val current = state.value

		if (current.uploading() || current is GroupState.Initial) {
			return
		}

		setState(current.withUploading(true))

		launchTry {
			val photoId = uploadPhotoUseCase(bytes, type)

			PhotoLocalDataSource.keepChatPhoto(chatId = current.chatId(), photoId = photoId)

			remember(photoId)

			setState(state.value.withUploading(false))
		} catch ::handleUploadFailure
	}

	private fun remember(photoId: String) {
		when (val current = state.value) {
			GroupState.Initial -> Unit
			is GroupState.Loading -> setState(current.copy(photo = photoId))
			is GroupState.Content -> setState(current.copy(photo = photoId))
			is GroupState.Error -> setState(current.copy(photo = photoId))
		}
	}

	private fun handleUploadFailure(error: Exception) {
		setState(state.value.withUploading(false))

		errorDelegate.processNonBlockingError(error)
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
					photo = current.photo,
					editing = current.editing,
					chatId = current.chatId,
					uploading = current.uploading,
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
					photo = current.photo,
					editing = current.editing,
					chatId = current.chatId,
					uploading = current.uploading,
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
					photo = current.photo,
					editing = current.editing,
					chatId = current.chatId,
					uploading = current.uploading,
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
					photo = current.photo,
					editing = current.editing,
					chatId = current.chatId,
					uploading = current.uploading,
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
					photo = current.photo,
					editing = current.editing,
					chatId = current.chatId,
					uploading = current.uploading,
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
				photo = current.photo,
				editing = current.editing,
				chatId = current.chatId,
				uploading = current.uploading,
			)

			is GroupState.Error -> handleFindPerson(
				title = current.title,
				description = current.description,
				login = current.login,
				adding = current.adding,
				members = current.members,
				photo = current.photo,
				editing = current.editing,
				chatId = current.chatId,
				uploading = current.uploading,
			)
		}
	}

	private fun handleFindPerson(
		title: DisplayNameValidationItem,
		description: String,
		login: UsernameValidationItem,
		adding: Boolean,
		members: List<ChatPerson>,
		photo: String,
		editing: Boolean,
		chatId: String,
		uploading: Boolean,
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
					photo = photo,
					editing = editing,
					chatId = chatId,
					uploading = uploading,
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
					photo = photo,
					editing = editing,
					chatId = chatId,
					uploading = uploading,
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
				photo = photo,
				editing = editing,
				chatId = chatId,
				uploading = uploading,
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
					photo = opened.photo,
					editing = opened.editing,
					chatId = opened.chatId,
					uploading = opened.uploading,
				),
			)
		} catch ::handleAddFailure
	}

	private fun handleSubmit() {
		if (state.value.editing()) {
			handleSave()

			return
		}

		when (val current = state.value) {
			GroupState.Initial,
			is GroupState.Loading,
			-> return
			is GroupState.Content -> handleCreateChat(current)
			is GroupState.Error -> handleCreateChat(current.form())
		}
	}

	private fun handleSave() {
		val opened = openedForm() ?: return
		val title = opened.title
		val description = opened.description
		val photo = opened.photo

		val checked = validateDisplayNameUseCase(title.data.trim())

		if (!checked.isValid()) {
			setState(
				GroupState.Content(
					title = checked,
					description = description,
					login = UsernameValidationItem(),
					adding = false,
					members = emptyList(),
					photo = photo,
					editing = opened.editing,
					chatId = opened.chatId,
					uploading = opened.uploading,
				),
			)

			return
		}

		val id = opened.chatId

		setState(
			GroupState.Loading(
				title = checked,
				description = description,
				login = UsernameValidationItem(),
				adding = false,
				members = emptyList(),
				photo = photo,
				editing = opened.editing,
				chatId = opened.chatId,
				uploading = opened.uploading,
			),
		)

		launchTry {
			val saved = updateChatUseCase(
				id,
				checked.data,
				description.trim(),
				photo,
			)

			if (photo.isNotEmpty()) {
				PhotoLocalDataSource.keepChatPhoto(chatId = id, photoId = photo)
			}

			setEvent(
				GroupEvent.Saved(
					title = saved.title,
					description = saved.description,
				),
			)

			router.navigateBack()
		} catch ::handleSaveFailure
	}

	private fun openedForm(): GroupState.Content? {
		return when (val current = state.value) {
			is GroupState.Content -> current
			is GroupState.Error -> GroupState.Content(
				title = current.title,
				description = current.description,
				login = current.login,
				adding = current.adding,
				members = current.members,
				photo = current.photo,
				editing = current.editing,
				chatId = current.chatId,
				uploading = current.uploading,
			)

			GroupState.Initial,
			is GroupState.Loading,
			-> null
		}
	}

	private fun handleSaveFailure(error: Exception) {
		handleRequestFailure(error)
	}

	private fun handleCreateChat(form: GroupState.Content) {
		val title = form.title
		val description = form.description
		val login = form.login
		val adding = form.adding
		val members = form.members
		val photo = form.photo
		val checked = validateDisplayNameUseCase(title.data.trim())

		if (!checked.isValid()) {
			setState(
				form.copy(title = checked),
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
				photo = photo,
				editing = form.editing,
				chatId = form.chatId,
				uploading = form.uploading,
			),
		)

		launchTry {
			val chat = createChatUseCase(
				ChatType.GROUP,
				checked.data,
				members.map { person -> person.username },
			)

			if (photo.isNotEmpty()) {
				updateChatUseCase(
					chat.id,
					checked.data,
					description.trim(),
					photo,
				)
				PhotoLocalDataSource.keepChatPhoto(chatId = chat.id, photoId = photo)
			}

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
					photo = failed.photo,
					editing = failed.editing,
					chatId = failed.chatId,
					uploading = failed.uploading,
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
				photo = failed.photo,
				editing = failed.editing,
				chatId = failed.chatId,
				uploading = failed.uploading,
				message = error.userMessage(),
			),
		)
	}
}

private fun GroupState.editing(): Boolean = when (this) {
	is GroupState.Content -> editing
	is GroupState.Loading -> editing
	is GroupState.Error -> editing
	GroupState.Initial -> false
}

private fun GroupState.chatId(): String = when (this) {
	is GroupState.Content -> chatId
	is GroupState.Loading -> chatId
	is GroupState.Error -> chatId
	GroupState.Initial -> ""
}

private fun GroupState.uploading(): Boolean = when (this) {
	is GroupState.Content -> uploading
	is GroupState.Loading -> uploading
	is GroupState.Error -> uploading
	GroupState.Initial -> false
}

private fun GroupState.withUploading(value: Boolean): GroupState = when (this) {
	is GroupState.Content -> copy(uploading = value)
	is GroupState.Loading -> copy(uploading = value)
	is GroupState.Error -> copy(uploading = value)
	GroupState.Initial -> this
}

private fun GroupState.Error.form(): GroupState.Content = GroupState.Content(
	title = title,
	description = description,
	login = login,
	adding = adding,
	members = members,
	photo = photo,
	editing = editing,
	chatId = chatId,
	uploading = uploading,
)