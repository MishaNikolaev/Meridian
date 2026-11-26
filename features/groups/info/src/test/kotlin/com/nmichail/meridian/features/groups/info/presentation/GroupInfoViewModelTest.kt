package com.nmichail.meridian.features.groups.info.presentation

import com.nmichail.meridian.component.photopicker.domain.usecase.DownloadPhotoUseCase
import com.nmichail.meridian.component.photopicker.domain.usecase.LoadPhotoUseCase
import com.nmichail.meridian.shared.organization.domain.usecase.ReadOrganizationMembersUseCase
import com.nmichail.meridian.core.network.error.ForbiddenException
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.core.validation.username.ValidateUsernameUseCase
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatProfile
import com.nmichail.meridian.shared.chat.domain.usecase.FindPersonUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.LoadMessagesUseCase
import com.nmichail.meridian.shared.group.domain.usecase.AddChatMemberUseCase
import com.nmichail.meridian.shared.group.domain.usecase.CreateChatInviteUseCase
import com.nmichail.meridian.shared.group.domain.usecase.LeaveChatUseCase
import com.nmichail.meridian.shared.group.domain.usecase.PromoteChatUseCase
import com.nmichail.meridian.shared.group.domain.usecase.ReadChatProfileUseCase
import com.nmichail.meridian.shared.group.domain.usecase.RemoveChatMemberUseCase
import com.nmichail.meridian.shared.group.domain.usecase.SearchPeopleUseCase
import com.nmichail.meridian.shared.group.domain.usecase.TransferChatUseCase
import com.nmichail.meridian.features.groups.info.domain.MatchGroupMembersUseCase
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.testutils.doThrowSafe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class GroupInfoViewModelTest {
	private val readChatProfileUseCase: ReadChatProfileUseCase = mock()
	private val findPersonUseCase: FindPersonUseCase = mock()
	private val addChatMemberUseCase: AddChatMemberUseCase = mock()
	private val removeChatMemberUseCase: RemoveChatMemberUseCase = mock()
	private val transferChatUseCase: TransferChatUseCase = mock()
	private val promoteChatUseCase: PromoteChatUseCase = mock()
	private val leaveChatUseCase: LeaveChatUseCase = mock()
	private val validateUsernameUseCase: ValidateUsernameUseCase = mock()
	private val loadPhotoUseCase: LoadPhotoUseCase = mock()
	private val readOrganizationMembersUseCase: ReadOrganizationMembersUseCase = mock()
	private val searchPeopleUseCase: SearchPeopleUseCase = mock()
	private val createChatInviteUseCase: CreateChatInviteUseCase = mock()
	private val loadMessagesUseCase: LoadMessagesUseCase = mock()
	private val downloadPhotoUseCase: DownloadPhotoUseCase = mock()
	private val router: GroupInfoRouter = mock()
	private val viewModel = GroupInfoViewModel(
		readChatProfileUseCase,
		findPersonUseCase,
		addChatMemberUseCase,
		removeChatMemberUseCase,
		transferChatUseCase,
		promoteChatUseCase,
		leaveChatUseCase,
		validateUsernameUseCase,
		loadPhotoUseCase,
		readOrganizationMembersUseCase,
		searchPeopleUseCase,
		createChatInviteUseCase,
		loadMessagesUseCase,
		downloadPhotoUseCase,
		MatchGroupMembersUseCase(),
		router,
	)

	@Test
	fun `load EXPECT members`() = runTest {
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "",
				title = "Платформа",
				members = listOf("Волков Сергей"),
			),
		)

		assertEquals(
			GroupInfoState.Content(
				title = "Платформа",
				description = "",
				mates = listOf(
					GroupMate(
						name = "",
						self = true,
						admin = false,
						userId = "",
						owner = false,
					),
					GroupMate(
						name = "Волков Сергей",
						self = false,
						admin = false,
						userId = "",
						owner = false,
					),
				),
				menu = null,
				more = false,
				expanded = false,
				adding = false,
				login = UsernameValidationItem(),
				refused = false,
				failed = false,
				denied = 0,
				canEdit = true,
				candidates = emptyList(),
				query = "",
				media = false,
				files = emptyList(),
				tab = GroupTab.Media,
				photo = "",
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `load EXPECT owner name`() = runTest {
		whenever(readChatProfileUseCase.invoke("1")) doReturn ChatProfile(
			title = "Платформа",
			description = "",
			owner = "Волков Сергей Андреевич",
			members = emptyList(),
			selfId = "",
			photoId = "",
		)

		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = listOf("Волков Сергей"),
			),
		)

		assertEquals(
			"Волков Сергей Андреевич",
			(viewModel.state.value as GroupInfoState.Content).mates.first().name,
		)
	}

	@Test
	fun `make admin EXPECT admin`() = runTest {
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "",
				title = "Платформа",
				members = listOf("Волков Сергей"),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.MakeAdmin(name = "Волков Сергей"))

		assertEquals(
			true,
			(viewModel.state.value as GroupInfoState.Content).mates.last().admin,
		)
	}

	@Test
	fun `promote EXPECT use case`() = runTest {
		whenever(readChatProfileUseCase.invoke("1")) doReturn ChatProfile(
			title = "Платформа",
			description = "",
			owner = "Я",
			members = listOf(
				ChatPerson(userId = "me", username = "me", displayName = "Я", role = "OWNER"),
				ChatPerson(userId = "2", username = "volkov", displayName = "Волков Сергей", role = "MEMBER"),
			),
			selfId = "me",
			photoId = "",
		)
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.MakeAdmin(name = "Волков Сергей"))

		verify(promoteChatUseCase).invoke("1", "2")
	}

	@Test
	fun `promote EXPECT admin stored`() = runTest {
		whenever(readChatProfileUseCase.invoke("1")) doReturn ChatProfile(
			title = "Платформа",
			description = "",
			owner = "Я",
			members = listOf(
				ChatPerson(userId = "me", username = "me", displayName = "Я", role = "OWNER"),
				ChatPerson(userId = "2", username = "volkov", displayName = "Волков Сергей", role = "MEMBER"),
			),
			selfId = "me",
			photoId = "",
		)
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.MakeAdmin(name = "Волков Сергей"))

		assertEquals(
			true,
			(viewModel.state.value as GroupInfoState.Content).mates.first { mate -> mate.userId == "2" }.admin,
		)
	}

	@Test
	fun `edit EXPECT editor`() = runTest {
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Edit)

		verify(router).navigateToEdit(
			chatId = "",
			title = "Платформа",
			description = "",
		)
	}

	@Test
	fun `profile EXPECT person`() = runTest {
		whenever(readChatProfileUseCase.invoke("1")) doReturn ChatProfile(
			title = "Платформа",
			description = "",
			owner = "Я",
			members = listOf(
				ChatPerson(userId = "me", username = "me", displayName = "Я", role = "OWNER"),
				ChatPerson(userId = "2", username = "volkov", displayName = "Волков Сергей", role = "MEMBER"),
			),
			selfId = "me",
			photoId = "",
		)
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Profile(name = "Волков Сергей", corporate = true))

		verify(router).navigateToPerson(userId = "2", corporate = true)
	}

	@Test
	fun `transfer EXPECT use case`() = runTest {
		whenever(readChatProfileUseCase.invoke("1")) doReturn ChatProfile(
			title = "Платформа",
			description = "",
			owner = "Я",
			members = listOf(
				ChatPerson(userId = "me", username = "me", displayName = "Я", role = "OWNER"),
				ChatPerson(userId = "2", username = "volkov", displayName = "Волков Сергей", role = "MEMBER"),
			),
			selfId = "me",
			photoId = "",
		)
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Transfer(name = "Волков Сергей"))

		verify(transferChatUseCase).invoke("1", "2")
	}

	@Test
	fun `transfer EXPECT self is not owner`() = runTest {
		whenever(readChatProfileUseCase.invoke("1")) doReturn ChatProfile(
			title = "Платформа",
			description = "",
			owner = "Я",
			members = listOf(
				ChatPerson(userId = "me", username = "me", displayName = "Я", role = "OWNER"),
				ChatPerson(userId = "2", username = "volkov", displayName = "Волков Сергей", role = "MEMBER"),
			),
			selfId = "me",
			photoId = "",
		)
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Transfer(name = "Волков Сергей"))

		val mates = (viewModel.state.value as GroupInfoState.Content).mates
		assertEquals(false, mates.first { mate -> mate.self }.owner)
	}

	@Test
	fun `transfer EXPECT self stays admin`() = runTest {
		whenever(readChatProfileUseCase.invoke("1")) doReturn ChatProfile(
			title = "Платформа",
			description = "",
			owner = "Я",
			members = listOf(
				ChatPerson(userId = "me", username = "me", displayName = "Я", role = "OWNER"),
				ChatPerson(userId = "2", username = "volkov", displayName = "Волков Сергей", role = "MEMBER"),
			),
			selfId = "me",
			photoId = "",
		)
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Transfer(name = "Волков Сергей"))

		val mates = (viewModel.state.value as GroupInfoState.Content).mates
		assertEquals(true, mates.first { mate -> mate.self }.admin)
	}

	@Test
	fun `transfer EXPECT member becomes owner`() = runTest {
		whenever(readChatProfileUseCase.invoke("1")) doReturn ChatProfile(
			title = "Платформа",
			description = "",
			owner = "Я",
			members = listOf(
				ChatPerson(userId = "me", username = "me", displayName = "Я", role = "OWNER"),
				ChatPerson(userId = "2", username = "volkov", displayName = "Волков Сергей", role = "MEMBER"),
			),
			selfId = "me",
			photoId = "",
		)
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Transfer(name = "Волков Сергей"))

		val mates = (viewModel.state.value as GroupInfoState.Content).mates
		assertEquals(true, mates.first { mate -> mate.userId == "2" }.owner)
	}

	@Test
	fun `remove EXPECT no call`() = runTest {
		whenever(readChatProfileUseCase.invoke("1")) doReturn ChatProfile(
			title = "Платформа",
			description = "",
			owner = "Волков Сергей",
			members = listOf(
				ChatPerson(userId = "me", username = "me", displayName = "Я", role = "ADMIN"),
				ChatPerson(userId = "2", username = "volkov", displayName = "Волков Сергей", role = "OWNER"),
			),
			selfId = "me",
			photoId = "",
		)
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Remove(name = "Волков Сергей"))

		verify(removeChatMemberUseCase, never()).invoke("1", "2")
	}

	@Test
	fun `remove EXPECT denied`() = runTest {
		whenever(readChatProfileUseCase.invoke("1")) doReturn ChatProfile(
			title = "Платформа",
			description = "",
			owner = "Волков Сергей",
			members = listOf(
				ChatPerson(userId = "me", username = "me", displayName = "Я", role = "ADMIN"),
				ChatPerson(userId = "2", username = "volkov", displayName = "Волков Сергей", role = "OWNER"),
			),
			selfId = "me",
			photoId = "",
		)
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Remove(name = "Волков Сергей"))

		assertEquals(1, (viewModel.state.value as GroupInfoState.Content).denied)
	}

	@Test
	fun `remove EXPECT owner stays`() = runTest {
		whenever(readChatProfileUseCase.invoke("1")) doReturn ChatProfile(
			title = "Платформа",
			description = "",
			owner = "Волков Сергей",
			members = listOf(
				ChatPerson(userId = "me", username = "me", displayName = "Я", role = "ADMIN"),
				ChatPerson(userId = "2", username = "volkov", displayName = "Волков Сергей", role = "OWNER"),
			),
			selfId = "me",
			photoId = "",
		)
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Remove(name = "Волков Сергей"))
		val shown = viewModel.state.value as GroupInfoState.Content

		assertEquals(true, shown.mates.any { mate -> mate.userId == "2" })
	}

	@Test
	fun `remove EXPECT member cannot`() = runTest {
		whenever(readChatProfileUseCase.invoke("1")) doReturn ChatProfile(
			title = "Платформа",
			description = "",
			owner = "Волков Сергей",
			members = listOf(
				ChatPerson(userId = "me", username = "me", displayName = "Я", role = "MEMBER"),
				ChatPerson(userId = "2", username = "volkov", displayName = "Волков Сергей", role = "OWNER"),
			),
			selfId = "me",
			photoId = "",
		)
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Remove(name = "Волков Сергей"))
		viewModel.applyIntent(GroupInfoIntent.StartAdd)

		verify(removeChatMemberUseCase, never()).invoke("1", "2")
		assertEquals(false, (viewModel.state.value as GroupInfoState.Content).adding)
	}

	@Test
	fun `leave EXPECT leave call`() = runTest {
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Leave)

		verify(leaveChatUseCase).invoke("1")
	}

	@Test
	fun `leave EXPECT chat closed`() = runTest {
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Leave)

		verify(router).exitChat()
	}

	@Test
	fun `leave EXPECT no close`() = runTest {
		whenever(leaveChatUseCase.invoke("1")) doThrowSafe ForbiddenException(httpStatus = 403)
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Leave)

		verify(router, never()).exitChat()
	}

	@Test
	fun `leave EXPECT owner stays`() = runTest {
		whenever(readChatProfileUseCase.invoke("1")) doReturn ChatProfile(
			title = "Платформа",
			description = "",
			owner = "",
			members = listOf(
				ChatPerson(userId = "me", username = "me", displayName = "Я", role = "OWNER"),
			),
			selfId = "me",
			photoId = "",
		)
		whenever(leaveChatUseCase.invoke("1")) doThrowSafe ForbiddenException(httpStatus = 403)
		viewModel.applyIntent(
			GroupInfoIntent.Load(
				chatId = "1",
				title = "Платформа",
				members = emptyList(),
			),
		)

		viewModel.applyIntent(GroupInfoIntent.Leave)

		assertEquals(true, (viewModel.state.value as GroupInfoState.Content).refused)
	}
}