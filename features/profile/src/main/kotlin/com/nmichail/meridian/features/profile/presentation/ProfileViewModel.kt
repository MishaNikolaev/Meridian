package com.nmichail.meridian.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmichail.meridian.core.presentation.launchTry
import com.nmichail.meridian.shared.session.domain.usecase.LogoutUseCase
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
	private val logoutUseCase: LogoutUseCase,
	private val router: ProfileRouter,
) : ViewModel() {

	private var loggingOut = false

	fun logout() {
		if (loggingOut) {
			return
		}
		loggingOut = true
		viewModelScope.launchTry {
			logoutUseCase()
			router.openWorkspace()
		} catch { _ ->
			router.openWorkspace()
		}
	}
}
