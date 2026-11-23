package com.nmichail.meridian.features.profile.presentation

import com.nmichail.meridian.component.photopicker.domain.usecase.InstallPhotoUseCase
import com.nmichail.meridian.component.photopicker.domain.usecase.KeepPersonPhotoUseCase
import com.nmichail.meridian.core.network.error.presentation.BlockingErrorProcessResult
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.features.profile.domain.usecase.LoadProfileUseCase
import com.nmichail.meridian.features.profile.domain.usecase.SaveStatusUseCase
import com.nmichail.meridian.shared.session.domain.usecase.LogoutUseCase
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
	private val loadProfileUseCase: LoadProfileUseCase,
	private val installPhotoUseCase: InstallPhotoUseCase,
	private val keepPersonPhotoUseCase: KeepPersonPhotoUseCase,
	private val saveStatusUseCase: SaveStatusUseCase,
	private val logoutUseCase: LogoutUseCase,
	private val errorDelegate: ErrorDelegate,
	private val router: ProfileRouter,
) : BaseViewModel<ProfileState, ProfileIntent, Nothing>() {

	private companion object {
		const val STATUS_LIMIT = 140
	}

	override fun initState(): ProfileState = ProfileState.Initial(loggingOut = false)

	override fun applyIntent(intent: ProfileIntent) {
		when (intent) {
			is ProfileIntent.LoadInitialData  -> handleLoadInitialData()
			is ProfileIntent.OpenAbout        -> handleOpenAbout()
			is ProfileIntent.Logout           -> handleLogout()
			is ProfileIntent.InstallPhoto     -> handleInstallPhoto(intent)
			is ProfileIntent.StartStatus      -> handleStartStatus()
			is ProfileIntent.ChangeStatus     -> handleChangeStatus(intent.value)
			is ProfileIntent.SaveStatus       -> handleSaveStatus()
			is ProfileIntent.DismissPhotoSaved -> handleDismissPhotoSaved()
			is ProfileIntent.OpenPlan         -> handleOpenPlan()
		}
	}

	private fun handleLoadInitialData() {
		val current = state.value

		if (current is ProfileState.Loading || current is ProfileState.Content) {
			return
		}

		setState(ProfileState.Loading(loggingOut = current.loggingOut()))

		launchTry {
			val profile = loadProfileUseCase()

			keepPersonPhotoUseCase(profile.userId, profile.photo)

			setState(
				ProfileState.Content(
					name = profile.name,
					email = profile.email,
					status = profile.status,
					statusDraft = null,
					position = profile.position,
					department = profile.department,
					host = profile.host,
					photo = profile.photo,
					userId = profile.userId,
					installing = false,
					savingStatus = false,
					photoSaved = false,
					loggingOut = state.value.loggingOut(),
				),
			)
		} catch ::handleLoadFailure
	}

	private fun handleInstallPhoto(intent: ProfileIntent.InstallPhoto) {
		val current = state.value as? ProfileState.Content ?: return

		if (current.installing) {
			return
		}

		setState(current.copy(installing = true, photoSaved = false))

		launchTry {
			val photo = installPhotoUseCase(intent.bytes, intent.type)
			val content = state.value as? ProfileState.Content ?: return@launchTry

			keepPersonPhotoUseCase(content.userId, photo)

			setState(content.copy(photo = photo, installing = false, photoSaved = true))
		} catch ::handleInstallFailure
	}

	private fun handleDismissPhotoSaved() {
		val current = state.value as? ProfileState.Content ?: return

		if (!current.photoSaved) {
			return
		}

		setState(current.copy(photoSaved = false))
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
		val status = current.statusDraft?.trim() ?: return

		if (current.savingStatus) {
			return
		}

		setState(current.copy(savingStatus = true))

		launchTry {
			val saved = saveStatusUseCase(status)
			val content = state.value as? ProfileState.Content ?: return@launchTry

			setState(content.copy(status = saved, statusDraft = null, savingStatus = false))
		} catch ::handleStatusFailure
	}

	private fun handleStatusFailure(error: Exception) {
		val current = state.value as? ProfileState.Content

		if (current != null) {
			setState(current.copy(savingStatus = false))
		}

		errorDelegate.processNonBlockingError(error)
	}

	private fun handleOpenAbout() {
		router.navigateToAbout()
	}

	private fun handleOpenPlan() {
		router.navigateToBusy()
	}

	private fun handleLogout() {
		if (state.value.loggingOut()) {
			return
		}

		setState(state.value.withLoggingOut())

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
			setState(
				ProfileState.Error(
					message = error.userMessage(),
					loggingOut = state.value.loggingOut(),
				),
			)
		}
	}
}

private fun ProfileState.loggingOut(): Boolean = when (this) {
	is ProfileState.Initial -> loggingOut
	is ProfileState.Loading -> loggingOut
	is ProfileState.Content -> loggingOut
	is ProfileState.Error -> loggingOut
}

private fun ProfileState.withLoggingOut(): ProfileState = when (this) {
	is ProfileState.Initial -> copy(loggingOut = true)
	is ProfileState.Loading -> copy(loggingOut = true)
	is ProfileState.Content -> copy(loggingOut = true)
	is ProfileState.Error -> copy(loggingOut = true)
}
