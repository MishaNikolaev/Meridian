package com.nmichail.meridian.features.profile.presentation

import com.nmichail.meridian.component.photopicker.domain.usecase.InstallPhotoUseCase
import com.nmichail.meridian.component.photopicker.domain.usecase.KeepPersonPhotoUseCase
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class ProfileViewModelTest {
	private val loadProfileUseCase: LoadProfileUseCase = mock()
	private val installPhotoUseCase: InstallPhotoUseCase = mock()
	private val keepPersonPhotoUseCase: KeepPersonPhotoUseCase = mock()
	private val saveStatusUseCase: SaveStatusUseCase = mock()
	private val logoutUseCase: LogoutUseCase = mock()
	private val router: ProfileRouter = mock()
	private val viewModel = ProfileViewModel(
		loadProfileUseCase = loadProfileUseCase,
		installPhotoUseCase = installPhotoUseCase,
		keepPersonPhotoUseCase = keepPersonPhotoUseCase,
		saveStatusUseCase = saveStatusUseCase,
		logoutUseCase = logoutUseCase,
		errorDelegate = ErrorDelegate(
			errorLogger = ErrorLogger(),
			defaultErrorReactionConverter = DefaultErrorReactionConverter(),
			defaultErrorEventConverter = DefaultErrorEventConverter(),
			errorRouter = mock<ErrorRouter>(),
			sessionRepository = mock<SessionRepository>(),
			deviceIdentityRepository = mock<DeviceIdentityRepository>(),
		),
		router = router,
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
	fun `load profile EXPECT profile`() = runTest {
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
				userId = account.userId,
				installing = false,
				savingStatus = false,
				photoSaved = false,
				loggingOut = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `load profile failure EXPECT error`() = runTest {
		whenever(loadProfileUseCase.invoke()) doThrowSafe IOException()

		viewModel.applyIntent(ProfileIntent.LoadInitialData)

		assertEquals(
			ProfileState.Error(
				message = NetworkR.string.error_no_connection,
				loggingOut = false,
			),
			viewModel.state.value,
		)
	}

	@Test
	fun `install photo EXPECT uploaded photo`() = runTest {
		val photoId = "photo-1"
		whenever(loadProfileUseCase.invoke()) doReturn account
		whenever(installPhotoUseCase.invoke(any(), any())) doReturn photoId
		viewModel.applyIntent(ProfileIntent.LoadInitialData)

		viewModel.applyIntent(ProfileIntent.InstallPhoto(bytes = byteArrayOf(1), type = "image/jpeg"))

		assertEquals(photoId, (viewModel.state.value as ProfileState.Content).photo)
	}

	@Test
	fun `install photo EXPECT photo saved`() = runTest {
		whenever(loadProfileUseCase.invoke()) doReturn account
		whenever(installPhotoUseCase.invoke(any(), any())) doReturn "photo-1"
		viewModel.applyIntent(ProfileIntent.LoadInitialData)

		viewModel.applyIntent(ProfileIntent.InstallPhoto(bytes = byteArrayOf(1), type = "image/jpeg"))

		assertEquals(true, (viewModel.state.value as ProfileState.Content).photoSaved)
	}

	@Test
	fun `install photo failure EXPECT photo unchanged`() = runTest {
		whenever(loadProfileUseCase.invoke()) doReturn account
		whenever(installPhotoUseCase.invoke(any(), any())) doThrowSafe IOException()
		viewModel.applyIntent(ProfileIntent.LoadInitialData)

		viewModel.applyIntent(ProfileIntent.InstallPhoto(bytes = byteArrayOf(1), type = "image/jpeg"))

		assertEquals("", (viewModel.state.value as ProfileState.Content).photo)
	}

	@Test
	fun `save status EXPECT status saved`() = runTest {
		val status = "На смене"
		whenever(loadProfileUseCase.invoke()) doReturn account
		whenever(saveStatusUseCase.invoke(status)) doReturn status
		viewModel.applyIntent(ProfileIntent.LoadInitialData)
		viewModel.applyIntent(ProfileIntent.StartStatus)
		viewModel.applyIntent(ProfileIntent.ChangeStatus(value = status))

		viewModel.applyIntent(ProfileIntent.SaveStatus)

		assertEquals(status, (viewModel.state.value as ProfileState.Content).status)
	}

	@Test
	fun `navigate to about EXPECT router method invocation`() = runTest {
		viewModel.applyIntent(ProfileIntent.OpenAbout)

		verify(router).navigateToAbout()
	}

	@Test
	fun `navigate to busy EXPECT router method invocation`() = runTest {
		viewModel.applyIntent(ProfileIntent.OpenPlan)

		verify(router).navigateToBusy()
	}

	@Test
	fun `log out EXPECT logout use case invocation`() = runTest {
		viewModel.applyIntent(ProfileIntent.Logout)

		verify(logoutUseCase).invoke()
	}

	@Test
	fun `log out EXPECT navigate to workspace`() = runTest {
		viewModel.applyIntent(ProfileIntent.Logout)

		verify(router).navigateToWorkspace()
	}

	@Test
	fun `second log out EXPECT single logout`() = runTest {
		viewModel.applyIntent(ProfileIntent.Logout)

		viewModel.applyIntent(ProfileIntent.Logout)

		verify(logoutUseCase, times(1)).invoke()
	}

	@Test
	fun `log out failure EXPECT navigate to workspace`() = runTest {
		whenever(logoutUseCase.invoke()) doThrowSafe IOException()

		viewModel.applyIntent(ProfileIntent.Logout)

		verify(router).navigateToWorkspace()
	}
}