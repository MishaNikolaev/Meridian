package com.nmichail.meridian.features.groups.presentation

import com.nmichail.meridian.component.photopicker.PhotoLocalDataSource
import com.nmichail.meridian.component.photopicker.domain.usecase.LoadPhotoUseCase
import com.nmichail.meridian.core.network.error.ForbiddenException
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.core.validation.username.ValidateUsernameUseCase
import com.nmichail.meridian.core.validation.username.isValid
import com.nmichail.meridian.shared.chat.domain.usecase.AddChatMemberUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.FindPersonUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.LeaveChatUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.PromoteChatUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadChatProfileUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.RemoveChatMemberUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.TransferChatUseCase
import javax.inject.Inject

private const val ROLE_OWNER = "OWNER"
private const val ROLE_ADMIN = "ADMIN"

class GroupInfoViewModel @Inject constructor(
	private val readChatProfileUseCase: ReadChatProfileUseCase,
	private val findPersonUseCase: FindPersonUseCase,
	private val addChatMemberUseCase: AddChatMemberUseCase,
	private val removeChatMemberUseCase: RemoveChatMemberUseCase,
	private val transferChatUseCase: TransferChatUseCase,
	private val promoteChatUseCase: PromoteChatUseCase,
	private val leaveChatUseCase: LeaveChatUseCase,
	private val validateUsernameUseCase: ValidateUsernameUseCase,
	private val loadPhotoUseCase: LoadPhotoUseCase,
	private val router: GroupRouter,
) : BaseViewModel<GroupInfoState, GroupInfoIntent, Nothing>() {
	private var chatId = ""
	private var generation = 0

	private var leaving = false

	override fun initState(): GroupInfoState = GroupInfoState.Initial

	override fun applyIntent(intent: GroupInfoIntent) {
		when (intent) {
			is GroupInfoIntent.Load -> handleLoad(intent)
			is GroupInfoIntent.Back -> router.navigateBack()
			is GroupInfoIntent.Member -> handleMember(intent.name)
			is GroupInfoIntent.Profile -> handleProfile(intent.name, intent.corporate)
			is GroupInfoIntent.Dismiss -> handleDismiss()
			is GroupInfoIntent.MakeAdmin -> handleAdmin(intent.name)
			is GroupInfoIntent.Remove -> handleRemove(intent.name)
			is GroupInfoIntent.Transfer -> handleTransfer(intent.name)
			is GroupInfoIntent.More -> handleMore()
			is GroupInfoIntent.Expand -> handleExpand()
			is GroupInfoIntent.Edit -> handleEdit()
			is GroupInfoIntent.StartAdd -> handleStartAdd()
			is GroupInfoIntent.ChangeLogin -> handleChangeLogin(intent.login)
			is GroupInfoIntent.ConfirmAdd -> handleConfirm()
			is GroupInfoIntent.Leave -> handleLeave()
			is GroupInfoIntent.Apply -> handleApply(intent.title, intent.description)
		}
	}

	private fun handleLoad(load: GroupInfoIntent.Load) {
		if (state.value is GroupInfoState.Content) {
			return
		}

		chatId = load.chatId

		generation += 1

		val ticket = generation

		val owner = GroupMate(
			name = "",
			self = true,
			admin = false,
			userId = "",
			owner = false,
		)

		val mates = load.members.map { name ->
			GroupMate(
				name = name,
				self = false,
				admin = false,
				userId = "",
				owner = false,
			)
		}

		setState(
			GroupInfoState.Content(
				title = load.title,
				description = "",
				mates = listOf(owner) + mates,
				menu = null,
				more = false,
				expanded = false,
				adding = false,
				login = UsernameValidationItem(),
				refused = false,
				failed = false,
				denied = 0,
				canEdit = true,
			),
		)

		refresh(ticket)
	}

	private fun refresh(ticket: Int) {
		val id = chatId

		if (id.isEmpty()) {
			return
		}

		launchTry {
			val profile = readChatProfileUseCase(id)

			if (ticket != generation) {
				return@launchTry
			}

			val current = state.value as? GroupInfoState.Content ?: return@launchTry

			val next = if (profile.members.isEmpty() || profile.selfId.isEmpty()) {
				current.mates.map { mate ->
					val name = if (mate.self && profile.owner.isNotEmpty()) profile.owner else mate.name

					mate.copy(name = name)
				}
			} else {
				profile.members.map { person ->
					GroupMate(
						name = person.displayName.ifBlank { person.username },
						self = person.userId == profile.selfId,
						admin = person.role == ROLE_ADMIN || person.role.isEmpty() && current.mates.any { mate ->
							mate.userId == person.userId && mate.admin
						},
						userId = person.userId,
						owner = person.role == ROLE_OWNER,
					)
				}
			}

			val me = next.find { mate -> mate.self }

			setState(
				current.copy(
					title = profile.title.ifBlank { current.title },
					description = profile.description,
					mates = next,
					canEdit = me != null && (me.self || me.admin),
				),
			)

			notePhotos(next)
		} catch { error ->
			setState(GroupInfoState.Error(message = error.userMessage()))
		}
	}

	private suspend fun notePhotos(mates: List<GroupMate>) {
		mates.forEach { mate ->
			if (mate.userId.isEmpty()) {
				return@forEach
			}

			val photo = loadPhotoUseCase(mate.userId)?.photoId.orEmpty()

			PhotoLocalDataSource.notePhoto(userId = mate.userId, photoId = photo)
		}
	}

	private fun keepShown(error: Exception) {
		if (state.value is GroupInfoState.Content) {
			return
		}

		setState(GroupInfoState.Error(message = error.userMessage()))
	}

	private fun handleDismiss() {
		val current = state.value as? GroupInfoState.Content ?: return

		setState(current.copy(menu = null, more = false))
	}

	private fun handleMore() {
		val current = state.value as? GroupInfoState.Content ?: return

		setState(current.copy(more = !current.more, menu = null))
	}

	private fun handleExpand() {
		val current = state.value as? GroupInfoState.Content ?: return

		setState(current.copy(expanded = true, menu = null, more = false))
	}

	private fun handleStartAdd() {
		val current = state.value as? GroupInfoState.Content ?: return

		setState(
			current.copy(
				adding = true,
				login = UsernameValidationItem(),
				menu = null,
				more = false,
			),
		)
	}

	private fun handleChangeLogin(login: String) {
		val current = state.value as? GroupInfoState.Content ?: return

		setState(current.copy(login = UsernameValidationItem(data = login)))
	}

	private fun handleApply(title: String, description: String) {
		generation += 1

		val current = state.value as? GroupInfoState.Content ?: return

		setState(
			current.copy(
				title = title,
				description = description,
				menu = null,
				more = false,
			),
		)
	}

	private fun handleMember(name: String) {
		val current = state.value as? GroupInfoState.Content ?: return

		val mate = current.mates.find { person -> person.name == name && !person.self }

		if (mate == null) {
			return
		}

		setState(
			current.copy(
				menu = name.takeIf { current.menu != name },
				more = false,
			),
		)
	}

	private fun handleProfile(name: String, corporate: Boolean) {
		val current = state.value as? GroupInfoState.Content ?: return

		val mate = current.mates.find { person -> person.name == name && !person.self && person.userId.isNotEmpty() } ?: return

		setState(current.copy(menu = null, more = false))

		router.navigateToPerson(userId = mate.userId, corporate = corporate)
	}

	private fun handleAdmin(name: String) {
		val current = state.value as? GroupInfoState.Content ?: return

		val mate = current.mates.find { person -> person.name == name && !person.self && !person.owner } ?: return

		if (mate.userId.isEmpty() || chatId.isEmpty()) {
			setState(
				current.copy(
					mates = current.mates.map { person ->
						if (person.name == name && !person.self) person.copy(admin = true) else person
					},
					menu = null,
				),
			)

			return
		}

		launchTry {
			promoteChatUseCase(chatId, mate.userId)

			val shown = state.value as? GroupInfoState.Content ?: return@launchTry

			setState(
				shown.copy(
					mates = shown.mates.map { person ->
						if (person.userId == mate.userId) person.copy(admin = true) else person
					},
					menu = null,
				),
			)
		} catch ::keepShown
	}

	private fun handleRemove(name: String) {
		val current = state.value as? GroupInfoState.Content ?: return

		val mate = current.mates.find { person -> person.name == name && !person.self } ?: return

		if (mate.owner) {
			setState(current.copy(menu = null, denied = current.denied + 1))

			return
		}

		if (mate.userId.isEmpty() || chatId.isEmpty()) {
			setState(
				current.copy(
					mates = current.mates.filter { person -> person.self || person.name != name },
					menu = null,
				),
			)

			return
		}

		launchTry {
			removeChatMemberUseCase(chatId, mate.userId)

			val shown = state.value as? GroupInfoState.Content ?: return@launchTry

			setState(
				shown.copy(
					mates = shown.mates.filter { person -> person.userId != mate.userId },
					menu = null,
				),
			)
		} catch ::refuseRemove
	}

	private fun refuseRemove(error: Exception) {
		if (error is ForbiddenException) {
			val current = state.value as? GroupInfoState.Content ?: return

			setState(current.copy(menu = null, denied = current.denied + 1))

			return
		}

		keepShown(error)
	}

	private fun handleTransfer(name: String) {
		val current = state.value as? GroupInfoState.Content ?: return

		val mate = current.mates.find { person -> person.name == name && !person.self } ?: return

		if (mate.userId.isEmpty() || chatId.isEmpty()) {
			setState(current.copy(menu = null, failed = true))

			return
		}

		setState(current.copy(menu = null, failed = false))

		launchTry {
			transferChatUseCase(chatId, mate.userId)

			val shown = state.value as? GroupInfoState.Content ?: return@launchTry

			setState(
				shown.copy(
					mates = shown.mates.map { person ->
						when {
							person.userId == mate.userId -> person.copy(owner = true, admin = false)
							person.self -> person.copy(owner = false, admin = true)
							else -> person
						}
					},
					menu = null,
				),
			)
		} catch {
			val shown = state.value as? GroupInfoState.Content ?: return@catch

			setState(shown.copy(failed = true))
		}
	}

	private fun handleConfirm() {
		val current = state.value as? GroupInfoState.Content ?: return

		val checked = validateUsernameUseCase(current.login.data.trim())

		if (!checked.isValid()) {
			setState(current.copy(login = checked))

			return
		}

		if (current.mates.any { mate -> mate.name.equals(checked.data, ignoreCase = true) }) {
			setState(current.copy(adding = false, login = UsernameValidationItem()))

			return
		}

		launchTry {
			val person = findPersonUseCase(checked.data)
			addChatMemberUseCase(chatId, person.userId)
			val shown = state.value as? GroupInfoState.Content ?: return@launchTry
			val name = person.displayName.ifBlank { person.username }

			val mate = GroupMate(
				name = name,
				self = false,
				admin = false,
				userId = person.userId,
				owner = false,
			)

			setState(
				shown.copy(
					adding = false,
					login = UsernameValidationItem(),
					mates = shown.mates + mate,
				),
			)

			notePhotos(listOf(mate))
		} catch ::keepShown
	}

	private fun handleLeave() {
		if (chatId.isEmpty() || leaving) {
			return
		}

		leaving = true

		val current = state.value as? GroupInfoState.Content

		if (current != null) {
			setState(current.copy(refused = false))
		}

		launchTry {
			leaveChatUseCase(chatId)

			router.closeChat()
		} catch ::refuseLeave
	}

	private fun refuseLeave(error: Exception) {
		leaving = false

		if (error is ForbiddenException) {
			val current = state.value as? GroupInfoState.Content ?: return

			setState(current.copy(refused = true))

			return
		}

		keepShown(error)
	}

	private fun handleEdit() {
		val current = state.value as? GroupInfoState.Content ?: return

		if (!current.canEdit) {
			return
		}

		setState(current.copy(menu = null, more = false))

		router.navigateToEdit(
			chatId = chatId,
			title = current.title,
			description = current.description,
		)
	}
}