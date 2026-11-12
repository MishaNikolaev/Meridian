package com.nmichail.meridian.features.profile.presentation

import com.nmichail.meridian.component.photopicker.PhotoLocalDataSource
import com.nmichail.meridian.component.photopicker.domain.usecase.InstallPhotoUseCase
import com.nmichail.meridian.core.network.error.presentation.BlockingErrorProcessResult
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.features.profile.domain.usecase.LoadProfileUseCase
import com.nmichail.meridian.features.profile.domain.usecase.SaveStatusUseCase
import com.nmichail.meridian.shared.session.domain.usecase.LogoutUseCase
import javax.inject.Inject

private const val STATUS_LIMIT = 140

class ProfileViewModel @Inject constructor(
	private val loadProfileUseCase: LoadProfileUseCase,
	private val installPhotoUseCase: InstallPhotoUseCase,
	private val saveStatusUseCase: SaveStatusUseCase,
	private val logoutUseCase: LogoutUseCase,
	private val errorDelegate: ErrorDelegate,
	private val router: ProfileRouter,
) : BaseViewModel<ProfileState, ProfileIntent, Nothing>() {
	private var loggingOut = false

	private var savingStatus = false

	private var userId = ""

	override fun initState(): ProfileState = ProfileState.Initial

	override fun applyIntent(intent: ProfileIntent) {
		when (intent) {
			is ProfileIntent.LoadInitialData -> handleLoadInitialData()
			is ProfileIntent.Logout          -> handleLogout()
			is ProfileIntent.InstallPhoto    -> handleInstall(intent)
			is ProfileIntent.StartStatus     -> handleStartStatus()
			is ProfileIntent.ChangeStatus    -> handleChangeStatus(intent.value)
			is ProfileIntent.SaveStatus      -> handleSaveStatus()
			is ProfileIntent.DismissNotice   -> handleDismissNotice()
		}
	}

	private fun handleLoadInitialData() {
		val current = state.value

		if (current is ProfileState.Loading || current is ProfileState.Content) {
			return
		}

		setState(ProfileState.Loading)

		launchTry {
			handleProfile()
		} catch ::handleLoadFailure
	}

	private suspend fun handleProfile() {
		val account = loadProfileUseCase()

		userId = account.userId

		PhotoLocalDataSource.notePhoto(userId = account.userId, photoId = account.photo)

		setState(
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
		)
	}

	private fun handleInstall(intent: ProfileIntent.InstallPhoto) {
		val current = state.value as? ProfileState.Content ?: return

		if (current.installing) {
			return
		}

		setState(current.copy(installing = true, notice = false))

		launchTry {
			val photo = installPhotoUseCase(intent.bytes, intent.type)

			val shown = state.value as? ProfileState.Content ?: return@launchTry

			PhotoLocalDataSource.notePhoto(userId = userId, photoId = photo)

			setState(shown.copy(photo = photo, installing = false, notice = true))
		} catch ::handleInstallFailure
	}

	private fun handleDismissNotice() {
		val current = state.value as? ProfileState.Content ?: return

		if (!current.notice) {
			return
		}

		setState(current.copy(notice = false))
	}

	private fun handleInstallFailure(error: Exception) {
		val current = state.value as? ProfileState.Content

		if (current != null) {
			setState(current.copy(installing = false))
		}

		errorDelegate.processNonBlockingError(error)
	}

	private fun handleStartStatus() {
		val current = state.value as? ProfileState.Content ?: return

		if (current.statusDraft != null) {
			return
		}

		setState(current.copy(statusDraft = current.status))
	}

	private fun handleChangeStatus(value: String) {
		val current = state.value as? ProfileState.Content ?: return

		if (current.statusDraft == null) {
			return
		}

		setState(current.copy(statusDraft = value.take(n = STATUS_LIMIT)))
	}

	private fun handleSaveStatus() {
		val current = state.value as? ProfileState.Content ?: return

		val draft = current.statusDraft?.trim() ?: return

		if (savingStatus) {
			return
		}

		savingStatus = true

		launchTry {
			val saved = saveStatusUseCase(draft)

			val shown = state.value as? ProfileState.Content ?: return@launchTry

			setState(shown.copy(status = saved, statusDraft = null))

			savingStatus = false
		} catch ::handleStatusFailure
	}

	private fun handleStatusFailure(error: Exception) {
		savingStatus = false

		errorDelegate.processNonBlockingError(error)
	}

	private fun handleLogout() {
		if (loggingOut) {
			return
		}

		loggingOut = true

		launchTry {
			logoutUseCase()

			handleNavigateToWorkspace()
		} catch ::handleLogoutError
	}

	private fun handleNavigateToWorkspace() {
		router.navigateToWorkspace()
	}

	private fun handleLogoutError(error: Exception) {
		handleNavigateToWorkspace()
	}

	private fun handleLoadFailure(error: Exception) {
		val result = errorDelegate.processBlockingError(
			exception = error,
			retryIntent = ProfileIntent.LoadInitialData,
			backIntent = null,
		)

		if (result is BlockingErrorProcessResult.NotHandled<*>) {
			setState(ProfileState.Error(message = error.userMessage()))
		}
	}
}