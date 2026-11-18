package com.nmichail.meridian.features.groups.presentation

import com.nmichail.meridian.component.photopicker.PhotoLocalDataSource
import com.nmichail.meridian.component.photopicker.domain.usecase.DownloadPhotoUseCase
import com.nmichail.meridian.component.photopicker.domain.usecase.LoadPhotoUseCase
import com.nmichail.meridian.core.network.error.ForbiddenException
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.core.validation.username.ValidateUsernameUseCase
import com.nmichail.meridian.core.validation.username.isValid
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.organization.domain.entity.OrganizationMember
import com.nmichail.meridian.shared.organization.domain.usecase.ReadOrganizationMembersUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.AddChatMemberUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.FindPersonUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.LeaveChatUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.LoadMessagesUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.PromoteChatUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadChatProfileUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.RemoveChatMemberUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.TransferChatUseCase
import javax.inject.Inject

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
	private val readOrganizationMembersUseCase: ReadOrganizationMembersUseCase,
	private val loadMessagesUseCase: LoadMessagesUseCase,
	private val downloadPhotoUseCase: DownloadPhotoUseCase,
	private val router: GroupRouter,
) : BaseViewModel<GroupInfoState, GroupInfoIntent, GroupInfoEvent>() {

	private var chatId = ""
	private var generation = 0

	private var leaving = false

	override fun initState(): GroupInfoState = GroupInfoState.Initial

	override fun applyIntent(intent: GroupInfoIntent) {
		when (intent) {
			is GroupInfoIntent.Load        -> handleLoad(intent)
			is GroupInfoIntent.Back        -> handleNavigateBack()
			is GroupInfoIntent.CancelAdd   -> handleCancelAdd()
			is GroupInfoIntent.ChangeQuery -> handleQuery(intent.query)
			is GroupInfoIntent.Pick        -> handlePick(intent.userId)
			is GroupInfoIntent.Member      -> handleMember(intent.name)
			is GroupInfoIntent.Profile     -> handleProfile(intent.name, intent.corporate)
			is GroupInfoIntent.Dismiss     -> handleDismiss()
			is GroupInfoIntent.MakeAdmin   -> handleAdmin(intent.name)
			is GroupInfoIntent.Remove      -> handleRemove(intent.name)
			is GroupInfoIntent.Transfer    -> handleTransfer(intent.name)
			is GroupInfoIntent.More        -> handleMore()
			is GroupInfoIntent.Expand      -> handleExpand()
			is GroupInfoIntent.Edit        -> handleEdit()
			is GroupInfoIntent.StartAdd    -> handleStartAdd()
			is GroupInfoIntent.ChangeLogin -> handleChangeLogin(intent.login)
			is GroupInfoIntent.ConfirmAdd  -> handleConfirm()
			is GroupInfoIntent.Leave       -> handleLeave()
			is GroupInfoIntent.OpenMedia   -> handleOpenMedia()
			is GroupInfoIntent.CloseMedia  -> handleCloseMedia()
			is GroupInfoIntent.ChooseTab   -> handleTab(intent.tab)
			is GroupInfoIntent.OpenStored  -> handleOpenStored(intent.id, intent.name, intent.type)
			is GroupInfoIntent.PlayStored  -> handleDownloadFile(intent.id, intent.name, intent.type)
			is GroupInfoIntent.Apply       -> handleApply(intent.title, intent.description)
		}
	}

	private fun handleLoad(load: GroupInfoIntent.Load) {
		if (state.value is GroupInfoState.Content || state.value is GroupInfoState.Loading) {
			return
		}

		chatId = load.chatId

		generation += 1

		val ticket = generation

		if (load.chatId.isEmpty()) {
			setState(shown(title = load.title, description = "", mates = seeded(load.members), canEdit = true))
			return
		}

		setState(GroupInfoState.Loading)
		refresh(ticket = ticket, title = load.title, members = load.members)
	}

	private fun refresh(ticket: Int, title: String, members: List<String>) {
		val id = chatId

		if (id.isEmpty()) {
			return
		}

		launchTry {
			val profile = readChatProfileUseCase(id)

			if (ticket != generation) {
				return@launchTry
			}

			val known = (state.value as? GroupInfoState.Content)?.mates ?: seeded(members)
			val next = if (profile.members.isEmpty() || profile.selfId.isEmpty()) {
				known.map { mate ->
					val name = if (mate.self && profile.owner.isNotEmpty()) profile.owner else mate.name

					mate.copy(name = name)
				}
			} else {
				profile.members.map { person ->
					GroupMate(
						name = person.displayName.ifBlank { person.username },
						self = person.userId == profile.selfId,
						admin = person.role == "ADMIN" || person.role.isEmpty() && known.any { mate ->
							mate.userId == person.userId && mate.admin
						},
						userId = person.userId,
						owner = person.role == "OWNER",
					)
				}
			}
			val me = next.find { mate -> mate.self }

			setState(
				shown(
					title = profile.title.ifBlank { title },
					description = profile.description,
					mates = next,
					canEdit = me != null && (me.self || me.admin),
				),
			)

			keepPhotos(next)
		} catch { error ->
			setState(GroupInfoState.Error(message = error.userMessage()))
		}
	}

	private suspend fun keepPhotos(mates: List<GroupMate>) {
		mates.forEach { mate ->
			if (mate.userId.isEmpty()) {
				return@forEach
			}

			val photo = loadPhotoUseCase(mate.userId)?.photoId.orEmpty()

			PhotoLocalDataSource.keepPersonPhoto(userId = mate.userId, photoId = photo)
		}
	}

	private fun handleFailure(error: Exception) {
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

	private fun handleNavigateBack() {
		val current = state.value as? GroupInfoState.Content

		if (current != null && current.photo.isNotEmpty()) {
			setState(current.copy(photo = ""))

			return
		}

		if (current != null && current.media) {
			setState(current.copy(media = false))

			return
		}

		if (current != null && current.adding) {
			setState(current.copy(adding = false, query = ""))

			return
		}

		router.navigateBack()
	}

	private fun handleCancelAdd() {
		val current = state.value as? GroupInfoState.Content ?: return

		setState(current.copy(adding = false, query = ""))
	}

	private fun handleQuery(query: String) {
		val current = state.value as? GroupInfoState.Content ?: return

		setState(current.copy(query = query))
	}

	private fun handleStartAdd() {
		val current = state.value as? GroupInfoState.Content ?: return

		setState(
			current.copy(
				adding = true,
				query = "",
				login = UsernameValidationItem(),
				menu = null,
				more = false,
			),
		)

		launchTry {
			val people = readOrganizationMembersUseCase()
			val shown = state.value as? GroupInfoState.Content ?: return@launchTry

			setState(shown.copy(candidates = people))
			keepPhotos(people.map { person -> person.mate() })
		} catch ::handleFailure
	}

	private fun handlePick(userId: String) {
		val current = state.value as? GroupInfoState.Content ?: return
		val person = current.candidates.find { candidate -> candidate.userId == userId } ?: return

		if (current.mates.any { mate -> mate.userId == userId }) {
			return
		}

		if (chatId.isEmpty()) {
			setState(current.copy(mates = current.mates + person.mate()))

			return
		}

		launchTry {
			addChatMemberUseCase(chatId, userId)

			val shown = state.value as? GroupInfoState.Content ?: return@launchTry

			setState(shown.copy(mates = shown.mates + person.mate()))
			keepPhotos(listOf(person.mate()))
		} catch ::handleFailure
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
		} catch ::handleFailure
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

		handleFailure(error)
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
							person.self                  -> person.copy(owner = false, admin = true)
							else                         -> person
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

			keepPhotos(listOf(mate))
		} catch ::handleFailure
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

		handleFailure(error)
	}

	private fun handleOpenMedia() {
		val current = state.value as? GroupInfoState.Content ?: return

		setState(current.copy(media = true, menu = null, more = false))

		val id = chatId

		if (id.isEmpty()) {
			return
		}

		launchTry {
			val stored = loadMessagesUseCase(id).lines.mapNotNull { line -> line.stored() }
			val shown = state.value as? GroupInfoState.Content ?: return@launchTry

			setState(shown.copy(files = stored))
		} catch ::handleFailure
	}

	private fun handleCloseMedia() {
		val current = state.value as? GroupInfoState.Content ?: return

		setState(current.copy(media = false))
	}

	private fun handleTab(tab: GroupTab) {
		val current = state.value as? GroupInfoState.Content ?: return

		setState(current.copy(tab = tab))
	}

	private fun handleOpenStored(id: String, name: String, type: String) {
		val current = state.value as? GroupInfoState.Content ?: return

		if (type.startsWith("image/") || type.startsWith("video/")) {
			setState(current.copy(photo = id))

			return
		}

		handleDownloadFile(id, name, type)
	}

	private fun handleDownloadFile(id: String, name: String, type: String) {
		launchTry {
			setEvent(
				GroupInfoEvent.Opened(
					name = name,
					type = type,
					bytes = downloadPhotoUseCase(id),
				),
			)
		} catch ::handleFailure
	}

	private fun shown(title: String, description: String, mates: List<GroupMate>, canEdit: Boolean) = GroupInfoState.Content(
		title = title,
		description = description,
		mates = mates,
		menu = null,
		more = false,
		expanded = false,
		adding = false,
		login = UsernameValidationItem(),
		refused = false,
		failed = false,
		denied = 0,
		canEdit = canEdit,
		candidates = emptyList(),
		query = "",
		media = false,
		files = emptyList(),
		tab = GroupTab.Media,
		photo = "",
	)

	private fun seeded(members: List<String>): List<GroupMate> {
		val owner = GroupMate(
			name = "",
			self = true,
			admin = false,
			userId = "",
			owner = false,
		)
		val mates = members.map { name ->
			GroupMate(
				name = name,
				self = false,
				admin = false,
				userId = "",
				owner = false,
			)
		}

		return listOf(owner) + mates
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

private fun ChatLine.stored(): GroupFile? {
	if (deleted || fileId.isEmpty()) {
		return null
	}

	return GroupFile(id = fileId, name = fileName, type = fileType)
}

private fun OrganizationMember.mate(): GroupMate = GroupMate(
	name = displayName.ifBlank { username },
	self = false,
	admin = false,
	userId = userId,
	owner = false,
)