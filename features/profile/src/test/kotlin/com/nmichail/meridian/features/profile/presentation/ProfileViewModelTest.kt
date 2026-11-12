package com.nmichail.meridian.features.profile.presentation

import com.nmichail.meridian.component.photopicker.domain.usecase.InstallPhotoUseCase
import com.nmichail.meridian.core.network.R as NetworkR
import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorEventConverter
import com.nmichail.meridian.core.network.error.presentation.DefaultErrorReactionConverter
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.presentation.ErrorLogger
import com.nmichail.meridian.core.network.error.presentation.ErrorRouter
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.testutils.doThrowSafe
import com.nmichail.meridian.features.profile.domain.entity.ProfileAccount
import com.nmichail.meridian.features.profile.domain.usecase.LoadProfileUseCase
import com.nmichail.meridian.features.profile.domain.usecase.SaveStatusUseCase
import com.nmichail.meridian.shared.session.domain.usecase.LogoutUseCase
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class ProfileViewModelTest {
	private val loadProfileUseCase: LoadProfileUseCase = mock()
	private val installPhotoUseCase: InstallPhotoUseCase = mock()
	private val saveStatusUseCase: SaveStatusUseCase = mock()
	private val logoutUseCase: LogoutUseCase = mock()
	private val router: ProfileRouter = mock()
	private val viewModel = ProfileViewModel(
		loadProfileUseCase,
		installPhotoUseCase,
		saveStatusUseCase,
		logoutUseCase,
		ErrorDelegate(
			errorLogger = ErrorLogger(),
			defaultErrorReactionConverter = DefaultErrorReactionConverter(),
			defaultErrorEventConverter = DefaultErrorEventConverter(),
			errorRouter = mock<ErrorRouter>(),
			sessionRepository = mock<SessionRepository>(),
			deviceIdentityRepository = mock<DeviceIdentityRepository>(),
		),
		router,
	)
	private val account = ProfileAccount(
		name = "Волков Сергей Андреевич",
		email = "chief@name.ru",
		status = "",
		position = "Стажёр-программист",
		department = "Группа перспективных проектов",
		host = "meridian.ftc.ru",
		photo = "",
		userId = "chief",
	)

	@Test
	fun `load EXPECT account`() = runTest {
		whenever(loadProfileUseCase.invoke()) doReturn account

		viewModel.applyIntent(ProfileIntent.LoadInitialData)

		assertEquals(
			ProfileState.Content(
				name = account.name,
				email = account.email,
				status = account.status,
				statusDraft = null,
				position = account.position,
				department = account.department,
				host = account.host,
				photo = account.photo,
				installing = false,
				notice = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `load failure EXPECT error`() = runTest {
		whenever(loadProfileUseCase.invoke()) doThrowSafe IOException()

		viewModel.applyIntent(ProfileIntent.LoadInitialData)

		assertEquals(
			ProfileState.Error(message = NetworkR.string.error_no_connection),
			viewModel.state.value,
		)
	}

	@Test
	fun `install EXPECT photo`() = runTest {
		val photoId = "photo-1"
		whenever(loadProfileUseCase.invoke()) doReturn account
		whenever(installPhotoUseCase.invoke(any(), eq("image/jpeg"))) doReturn photoId
		viewModel.applyIntent(ProfileIntent.LoadInitialData)

		viewModel.applyIntent(ProfileIntent.InstallPhoto(bytes = byteArrayOf(1), type = "image/jpeg"))

		assertEquals(photoId, (viewModel.state.value as ProfileState.Content).photo)
	}

	@Test
	fun `install EXPECT notice`() = runTest {
		whenever(loadProfileUseCase.invoke()) doReturn account
		whenever(installPhotoUseCase.invoke(any(), eq("image/jpeg"))) doReturn "photo-1"
		viewModel.applyIntent(ProfileIntent.LoadInitialData)

		viewModel.applyIntent(ProfileIntent.InstallPhoto(bytes = byteArrayOf(1), type = "image/jpeg"))

		assertEquals(true, (viewModel.state.value as ProfileState.Content).notice)
	}

	@Test
	fun `install failure EXPECT photo stays`() = runTest {
		whenever(loadProfileUseCase.invoke()) doReturn account
		whenever(installPhotoUseCase.invoke(any(), eq("image/jpeg"))) doThrowSafe IOException()
		viewModel.applyIntent(ProfileIntent.LoadInitialData)

		viewModel.applyIntent(ProfileIntent.InstallPhoto(bytes = byteArrayOf(1), type = "image/jpeg"))

		assertEquals("", (viewModel.state.value as ProfileState.Content).photo)
	}

	@Test
	fun `save status EXPECT stored`() = runTest {
		whenever(loadProfileUseCase.invoke()) doReturn account
		whenever(saveStatusUseCase.invoke("На смене")) doReturn "На смене"
		viewModel.applyIntent(ProfileIntent.LoadInitialData)
		viewModel.applyIntent(ProfileIntent.StartStatus)
		viewModel.applyIntent(ProfileIntent.ChangeStatus(value = "На смене"))

		viewModel.applyIntent(ProfileIntent.SaveStatus)

		assertEquals("На смене", (viewModel.state.value as ProfileState.Content).status)
	}

	@Test
	fun `logout EXPECT logout use case`() = runTest {
		viewModel.applyIntent(ProfileIntent.Logout)

		verify(logoutUseCase).invoke()
	}

	@Test
	fun `logout EXPECT workspace`() = runTest {
		viewModel.applyIntent(ProfileIntent.Logout)

		verify(router).navigateToWorkspace()
	}

	@Test
	fun `second logout EXPECT single logout`() = runTest {
		viewModel.applyIntent(ProfileIntent.Logout)

		viewModel.applyIntent(ProfileIntent.Logout)

		verify(logoutUseCase, times(1)).invoke()
	}

	@Test
	fun `logout failure EXPECT workspace`() = runTest {
		whenever(logoutUseCase.invoke()) doThrowSafe IOException()

		viewModel.applyIntent(ProfileIntent.Logout)

		verify(router).navigateToWorkspace()
	}
}