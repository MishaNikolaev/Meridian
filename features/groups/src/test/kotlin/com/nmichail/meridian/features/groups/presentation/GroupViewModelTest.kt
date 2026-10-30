package com.nmichail.meridian.features.groups.presentation

import com.nmichail.meridian.core.network.R
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorEventConverter
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorReactionConverter
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.presentation.ErrorLogger
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.testutils.doThrowSafe
import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.displayName.DisplayNameInvalidState
import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.core.validation.displayName.ValidateDisplayNameUseCase
import com.nmichail.meridian.core.validation.username.UsernameInvalidState
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.core.validation.username.ValidateUsernameUseCase
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat
import com.nmichail.meridian.shared.chat.domain.usecase.CreateChatUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.FindPersonUseCase
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class GroupViewModelTest {

	private val findPersonUseCase: FindPersonUseCase = mock()
	private val createChatUseCase: CreateChatUseCase = mock()
	private val validateUsernameUseCase: ValidateUsernameUseCase = mock()
	private val validateDisplayNameUseCase: ValidateDisplayNameUseCase = mock()
	private val router: GroupRouter = mock()
	private val viewModel = GroupViewModel(
		findPersonUseCase,
		createChatUseCase,
		validateUsernameUseCase,
		validateDisplayNameUseCase,
		ErrorDelegate(
			errorLogger = ErrorLogger(),
			defaultErrorReactionConverter = DefaultErrorReactionConverter(),
			defaultErrorEventConverter = DefaultErrorEventConverter(),
		),
		router,
	)
	private val title = DisplayNameValidationItem(
		data = "Платформа",
		validationState = DefaultValidationState.Valid,
	)
	private val login = UsernameValidationItem(
		data = "chief",
		validationState = DefaultValidationState.Valid,
	)
	private val chief = ChatPerson(
		userId = "abea9342-21a2-43e3-9fd0-f20b51c9a81c",
		username = "chief",
		displayName = "Волков Сергей Андреевич",
	)
	private val member = ChatPerson(
		userId = "2f288e3d-c526-4f8d-b4bf-28c4779fd60a",
		username = "member",
		displayName = "Новикова Анна Павловна",
	)
	private val created = CreatedChat(
		id = "chat",
		title = "Платформа",
		corporate = false,
	)

	@Test
	fun `load EXPECT empty form`() = runTest {
		viewModel.applyIntent(GroupIntent.Load)

		assertEquals(
			GroupState.Content(
				title = DisplayNameValidationItem(),
				description = "",
				login = UsernameValidationItem(),
				adding = false,
				members = emptyList(),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `second load EXPECT form is kept`() = runTest {
		whenever(validateDisplayNameUseCase.invoke("Платформа")) doReturn title
		viewModel.applyIntent(GroupIntent.Load)
		viewModel.applyIntent(GroupIntent.ChangeTitle(title = "Платформа"))

		viewModel.applyIntent(GroupIntent.Load)

		assertEquals(
			GroupState.Content(
				title = title,
				description = "",
				login = UsernameValidationItem(),
				adding = false,
				members = emptyList(),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `change title EXPECT validated title`() = runTest {
		whenever(validateDisplayNameUseCase.invoke("Платформа")) doReturn title
		viewModel.applyIntent(GroupIntent.Load)

		viewModel.applyIntent(GroupIntent.ChangeTitle(title = "Платформа"))

		assertEquals(
			GroupState.Content(
				title = title,
				description = "",
				login = UsernameValidationItem(),
				adding = false,
				members = emptyList(),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `change description EXPECT description`() = runTest {
		viewModel.applyIntent(GroupIntent.Load)

		viewModel.applyIntent(GroupIntent.ChangeDescription(description = "о платформе"))

		assertEquals(
			GroupState.Content(
				title = DisplayNameValidationItem(),
				description = "о платформе",
				login = UsernameValidationItem(),
				adding = false,
				members = emptyList(),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `change login EXPECT validated login`() = runTest {
		whenever(validateUsernameUseCase.invoke("chief")) doReturn login
		viewModel.applyIntent(GroupIntent.Load)

		viewModel.applyIntent(GroupIntent.ChangeLogin(login = "chief"))

		assertEquals(
			GroupState.Content(
				title = DisplayNameValidationItem(),
				description = "",
				login = login,
				adding = false,
				members = emptyList(),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `open members EXPECT adding`() = runTest {
		viewModel.applyIntent(GroupIntent.Load)

		viewModel.applyIntent(GroupIntent.OpenMembers)

		assertEquals(
			GroupState.Content(
				title = DisplayNameValidationItem(),
				description = "",
				login = UsernameValidationItem(),
				adding = true,
				members = emptyList(),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `add invalid login EXPECT content`() = runTest {
		val invalid = UsernameValidationItem(
			data = "x",
			validationState = DefaultValidationState.Invalid(UsernameInvalidState.INVALID_LENGTH),
		)
		whenever(validateUsernameUseCase.invoke("x")) doReturn invalid
		viewModel.applyIntent(GroupIntent.Load)
		viewModel.applyIntent(GroupIntent.ChangeLogin(login = "x"))

		viewModel.applyIntent(GroupIntent.AddMember)

		assertEquals(
			GroupState.Content(
				title = DisplayNameValidationItem(),
				description = "",
				login = invalid,
				adding = false,
				members = emptyList(),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `add invalid login EXPECT person is not requested`() = runTest {
		val invalid = UsernameValidationItem(
			data = "x",
			validationState = DefaultValidationState.Invalid(UsernameInvalidState.INVALID_LENGTH),
		)
		whenever(validateUsernameUseCase.invoke("x")) doReturn invalid
		viewModel.applyIntent(GroupIntent.Load)
		viewModel.applyIntent(GroupIntent.ChangeLogin(login = "x"))

		viewModel.applyIntent(GroupIntent.AddMember)

		verify(findPersonUseCase, never()).invoke("x")
	}

	@Test
	fun `add member EXPECT member`() = runTest {
		whenever(validateUsernameUseCase.invoke("chief")) doReturn login
		whenever(findPersonUseCase.invoke("chief")) doReturn chief
		viewModel.applyIntent(GroupIntent.Load)
		viewModel.applyIntent(GroupIntent.ChangeLogin(login = "chief"))

		viewModel.applyIntent(GroupIntent.AddMember)

		assertEquals(
			GroupState.Content(
				title = DisplayNameValidationItem(),
				description = "",
				login = UsernameValidationItem(),
				adding = false,
				members = listOf(chief),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `add duplicate EXPECT login is cleared`() = runTest {
		whenever(validateUsernameUseCase.invoke("chief")) doReturn login
		whenever(findPersonUseCase.invoke("chief")) doReturn chief
		viewModel.applyIntent(GroupIntent.Load)
		viewModel.applyIntent(GroupIntent.ChangeLogin(login = "chief"))
		viewModel.applyIntent(GroupIntent.AddMember)
		viewModel.applyIntent(GroupIntent.ChangeLogin(login = "chief"))

		viewModel.applyIntent(GroupIntent.AddMember)

		assertEquals(
			GroupState.Content(
				title = DisplayNameValidationItem(),
				description = "",
				login = UsernameValidationItem(),
				adding = false,
				members = listOf(chief),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `add duplicate EXPECT person is requested once`() = runTest {
		whenever(validateUsernameUseCase.invoke("chief")) doReturn login
		whenever(findPersonUseCase.invoke("chief")) doReturn chief
		viewModel.applyIntent(GroupIntent.Load)
		viewModel.applyIntent(GroupIntent.ChangeLogin(login = "chief"))
		viewModel.applyIntent(GroupIntent.AddMember)
		viewModel.applyIntent(GroupIntent.ChangeLogin(login = "chief"))

		viewModel.applyIntent(GroupIntent.AddMember)

		verify(findPersonUseCase, times(1)).invoke("chief")
	}

	@Test
	fun `add member failure EXPECT error`() = runTest {
		whenever(validateUsernameUseCase.invoke("chief")) doReturn login
		whenever(findPersonUseCase.invoke("chief")) doThrowSafe IOException("down")
		viewModel.applyIntent(GroupIntent.Load)
		viewModel.applyIntent(GroupIntent.ChangeLogin(login = "chief"))

		viewModel.applyIntent(GroupIntent.AddMember)

		assertEquals(
			GroupState.Error(
				title = DisplayNameValidationItem(),
				description = "",
				login = login,
				adding = false,
				members = emptyList(),
				message = R.string.error_no_connection,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `remove member EXPECT member is gone`() = runTest {
		whenever(validateUsernameUseCase.invoke("chief")) doReturn login
		whenever(validateUsernameUseCase.invoke("member")) doReturn UsernameValidationItem(
			data = "member",
			validationState = DefaultValidationState.Valid,
		)
		whenever(findPersonUseCase.invoke("chief")) doReturn chief
		whenever(findPersonUseCase.invoke("member")) doReturn member
		viewModel.applyIntent(GroupIntent.Load)
		viewModel.applyIntent(GroupIntent.ChangeLogin(login = "chief"))
		viewModel.applyIntent(GroupIntent.AddMember)
		viewModel.applyIntent(GroupIntent.ChangeLogin(login = "member"))
		viewModel.applyIntent(GroupIntent.AddMember)

		viewModel.applyIntent(GroupIntent.RemoveMember(userId = chief.userId))

		assertEquals(
			GroupState.Content(
				title = DisplayNameValidationItem(),
				description = "",
				login = UsernameValidationItem(),
				adding = false,
				members = listOf(member),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `submit invalid title EXPECT content`() = runTest {
		val invalid = DisplayNameValidationItem(
			data = "",
			validationState = DefaultValidationState.Invalid(DisplayNameInvalidState.EMPTY),
		)
		whenever(validateDisplayNameUseCase.invoke("")) doReturn invalid
		viewModel.applyIntent(GroupIntent.Load)

		viewModel.applyIntent(GroupIntent.Submit)

		assertEquals(
			GroupState.Content(
				title = invalid,
				description = "",
				login = UsernameValidationItem(),
				adding = false,
				members = emptyList(),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `submit invalid title EXPECT chat is not created`() = runTest {
		val invalid = DisplayNameValidationItem(
			data = "",
			validationState = DefaultValidationState.Invalid(DisplayNameInvalidState.EMPTY),
		)
		whenever(validateDisplayNameUseCase.invoke("")) doReturn invalid
		viewModel.applyIntent(GroupIntent.Load)

		viewModel.applyIntent(GroupIntent.Submit)

		verify(createChatUseCase, never()).invoke(ChatType.GROUP, "", emptyList())
	}

	@Test
	fun `submit EXPECT navigate back`() = runTest {
		whenever(validateDisplayNameUseCase.invoke("Платформа")) doReturn title
		whenever(
			createChatUseCase.invoke(
				ChatType.GROUP,
				"Платформа",
				emptyList(),
			),
		) doReturn created
		viewModel.applyIntent(GroupIntent.Load)
		viewModel.applyIntent(GroupIntent.ChangeTitle(title = "Платформа"))

		viewModel.applyIntent(GroupIntent.Submit)

		verify(router).navigateBack()
	}

	@Test
	fun `submit EXPECT conversation`() = runTest {
		whenever(validateDisplayNameUseCase.invoke("Платформа")) doReturn title
		whenever(
			createChatUseCase.invoke(
				ChatType.GROUP,
				"Платформа",
				emptyList(),
			),
		) doReturn created
		viewModel.applyIntent(GroupIntent.Load)
		viewModel.applyIntent(GroupIntent.ChangeTitle(title = "Платформа"))

		viewModel.applyIntent(GroupIntent.Submit)

		verify(router).navigateToConversation(
			chatId = "chat",
			title = "Платформа",
			corporate = false,
			personId = "",
		)
	}

	@Test
	fun `submit failure EXPECT error`() = runTest {
		whenever(validateDisplayNameUseCase.invoke("Платформа")) doReturn title
		whenever(
			createChatUseCase.invoke(
				ChatType.GROUP,
				"Платформа",
				emptyList(),
			),
		) doThrowSafe IOException("down")
		viewModel.applyIntent(GroupIntent.Load)
		viewModel.applyIntent(GroupIntent.ChangeTitle(title = "Платформа"))

		viewModel.applyIntent(GroupIntent.Submit)

		assertEquals(
			GroupState.Error(
				title = title,
				description = "",
				login = UsernameValidationItem(),
				adding = false,
				members = emptyList(),
				message = R.string.error_no_connection,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `change description after error EXPECT content`() = runTest {
		whenever(validateDisplayNameUseCase.invoke("Платформа")) doReturn title
		whenever(
			createChatUseCase.invoke(
				ChatType.GROUP,
				"Платформа",
				emptyList(),
			),
		) doThrowSafe IOException("down")
		viewModel.applyIntent(GroupIntent.Load)
		viewModel.applyIntent(GroupIntent.ChangeTitle(title = "Платформа"))
		viewModel.applyIntent(GroupIntent.Submit)

		viewModel.applyIntent(GroupIntent.ChangeDescription(description = "о платформе"))

		assertEquals(
			GroupState.Content(
				title = title,
				description = "о платформе",
				login = UsernameValidationItem(),
				adding = false,
				members = emptyList(),
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `navigate back EXPECT router back`() = runTest {
		viewModel.applyIntent(GroupIntent.NavigateBack)

		verify(router).navigateBack()
	}
}
