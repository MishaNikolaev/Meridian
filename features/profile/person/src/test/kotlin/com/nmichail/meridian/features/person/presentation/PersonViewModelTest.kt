package com.nmichail.meridian.features.person.presentation

import com.nmichail.meridian.core.network.R
import com.nmichail.meridian.core.validation.capitalize.CapitalizeUseCase
import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorEventConverter
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorReactionConverter
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.presentation.ErrorLogger
import com.nmichail.meridian.core.network.error.presentation.ErrorRouter
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.testutils.doThrowSafe
import com.nmichail.meridian.features.person.domain.entity.PersonProfile
import com.nmichail.meridian.component.photopicker.domain.usecase.KeepPersonPhotoUseCase
import com.nmichail.meridian.features.person.domain.usecase.LoadPersonUseCase
import com.nmichail.meridian.features.person.domain.usecase.ReadPersonUseCase
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat
import com.nmichail.meridian.shared.chat.domain.usecase.CreateChatUseCase
import com.nmichail.meridian.shared.chat.domain.usecase.ReadChatsUseCase
import com.nmichail.meridian.shared.session.domain.usecase.GetUserIdUseCase
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
class PersonViewModelTest {
	private val loadPersonUseCase: LoadPersonUseCase = mock()
	private val readPersonUseCase: ReadPersonUseCase = mock()
	private val keepPersonPhotoUseCase: KeepPersonPhotoUseCase = mock()
	private val createChatUseCase: CreateChatUseCase = mock()
	private val readChatsUseCase: ReadChatsUseCase = mock()
	private val router: PersonRouter = mock()
	private val getUserIdUseCase: GetUserIdUseCase = mock()
	private val capitalizeUseCase = CapitalizeUseCase()
	private val viewModel = PersonViewModel(
		loadPersonUseCase,
		readPersonUseCase,
		keepPersonPhotoUseCase,
		createChatUseCase,
		readChatsUseCase,
		ErrorDelegate(
			errorLogger = ErrorLogger(),
			defaultErrorReactionConverter = DefaultErrorReactionConverter(),
			defaultErrorEventConverter = DefaultErrorEventConverter(),
			errorRouter = mock<ErrorRouter>(),
			sessionRepository = mock<SessionRepository>(),
			deviceIdentityRepository = mock<DeviceIdentityRepository>(),
		),
		router,
		getUserIdUseCase,
		capitalizeUseCase,
	)
	private val card = PersonProfile(
		name = "Волков Сергей Андреевич",
		position = "ведущий инженер-программист мобильных приложений",
		department = "Мобильная разработка",
		office = "Новосибирск, М. Малкова 54",
		server = "meridian.name.ru",
		status = "в сети",
		manager = "Новикова Анна Павловна",
		managerId = "2f288e3d-c526-4f8d-b4bf-28c4779fd60a",
		email = "chief@name.ru",
		phone = "",
		photo = "",
	)

	@Test
	fun `load EXPECT card`() = runTest {
		whenever(loadPersonUseCase.invoke("chief")) doReturn card

		viewModel.applyIntent(
			PersonIntent.Load(
				userId = "chief",
				corporate = true,
			),
		)

		assertEquals(
			PersonState.Content(
				userId = "chief",
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
				corporate = true,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `cached person EXPECT kept when reload fails`() = runTest {
		whenever(readPersonUseCase.invoke("chief")) doReturn card
		whenever(loadPersonUseCase.invoke("chief")) doThrowSafe IOException("down")

		viewModel.applyIntent(
			PersonIntent.Load(
				userId = "chief",
				corporate = true,
			),
		)

		assertEquals(
			PersonState.Content(
				userId = "chief",
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
				corporate = true,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `load failure EXPECT error`() = runTest {
		whenever(loadPersonUseCase.invoke("chief")) doThrowSafe IOException("down")

		viewModel.applyIntent(
			PersonIntent.Load(
				userId = "chief",
				corporate = true,
			),
		)

		assertEquals(
			PersonState.Error(message = R.string.error_no_connection),
			viewModel.state.value,
		)
	}

	@Test
	fun `second load EXPECT single request`() = runTest {
		whenever(loadPersonUseCase.invoke("chief")) doReturn card
		viewModel.applyIntent(
			PersonIntent.Load(
				userId = "chief",
				corporate = true,
			),
		)

		viewModel.applyIntent(
			PersonIntent.Load(
				userId = "chief",
				corporate = true,
			),
		)

		verify(loadPersonUseCase, times(1)).invoke("chief")
	}

	@Test
	fun `load after error EXPECT card`() = runTest {
		whenever(loadPersonUseCase.invoke("chief")) doThrowSafe IOException("down")
		viewModel.applyIntent(
			PersonIntent.Load(
				userId = "chief",
				corporate = true,
			),
		)
		doReturn(card).whenever(loadPersonUseCase).invoke("chief")

		viewModel.applyIntent(
			PersonIntent.Load(
				userId = "chief",
				corporate = true,
			),
		)

		assertEquals(
			PersonState.Content(
				userId = "chief",
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
				corporate = true,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `back EXPECT router back`() = runTest {
		viewModel.applyIntent(PersonIntent.Back)

		verify(router).navigateBack()
	}

	@Test
	fun `write EXPECT direct chat`() = runTest {
		whenever(loadPersonUseCase.invoke("chief")) doReturn card
		whenever(createChatUseCase.invoke(ChatType.DIRECT, "", listOf("chief"))) doReturn CreatedChat(
			id = "chat-1",
			title = "Волков Сергей Андреевич",
			corporate = true,
		)
		viewModel.applyIntent(
			PersonIntent.Load(
				userId = "chief",
				corporate = true,
			),
		)

		viewModel.applyIntent(PersonIntent.Write(userId = "chief"))

		verify(router).navigateToConversation(
			chatId = "chat-1",
			title = "Волков Сергей Андреевич",
			corporate = true,
			personId = "chief",
		)
	}

	@Test
	fun `call EXPECT router navigates to audio call`() = runTest {
		whenever(loadPersonUseCase.invoke("chief")) doReturn card
		whenever(createChatUseCase.invoke(ChatType.DIRECT, "", listOf("chief"))) doReturn CreatedChat(
			id = "chat-1",
			title = "Волков Сергей Андреевич",
			corporate = true,
		)
		viewModel.applyIntent(
			PersonIntent.Load(
				userId = "chief",
				corporate = true,
			),
		)

		viewModel.applyIntent(PersonIntent.Call(userId = "chief"))

		verify(router).navigateToCall(
			chatId = "chat-1",
			title = "Волков Сергей Андреевич",
			personId = "chief",
			video = false,
		)
	}

	@Test
	fun `video EXPECT router navigates to video call`() = runTest {
		whenever(loadPersonUseCase.invoke("chief")) doReturn card
		whenever(createChatUseCase.invoke(ChatType.DIRECT, "", listOf("chief"))) doReturn CreatedChat(
			id = "chat-1",
			title = "Волков Сергей Андреевич",
			corporate = true,
		)
		viewModel.applyIntent(
			PersonIntent.Load(
				userId = "chief",
				corporate = true,
			),
		)

		viewModel.applyIntent(PersonIntent.Video(userId = "chief"))

		verify(router).navigateToCall(
			chatId = "chat-1",
			title = "Волков Сергей Андреевич",
			personId = "chief",
			video = true,
		)
	}

	@Test
	fun `open manager EXPECT person`() = runTest {
		whenever(loadPersonUseCase.invoke("chief")) doReturn card
		viewModel.applyIntent(
			PersonIntent.Load(
				userId = "chief",
				corporate = true,
			),
		)

		viewModel.applyIntent(PersonIntent.OpenManager)

		verify(router).navigateToPerson(
			userId = card.managerId,
			corporate = true,
		)
	}

	@Test
	fun `open manager without id EXPECT stay`() = runTest {
		whenever(loadPersonUseCase.invoke("chief")) doReturn card.copy(managerId = "")
		viewModel.applyIntent(
			PersonIntent.Load(
				userId = "chief",
				corporate = true,
			),
		)

		viewModel.applyIntent(PersonIntent.OpenManager)

		verify(router, never()).navigateToPerson(
			userId = "",
			corporate = true,
		)
	}
}