package com.nmichail.meridian.features.calls.catalog.presentation

import com.nmichail.meridian.component.photopicker.domain.usecase.LoadPhotoUseCase
import com.nmichail.meridian.component.photopicker.domain.usecase.NoteFacesUseCase
import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.features.calls.catalog.domain.usecase.LoadCallsUseCase
import com.nmichail.meridian.features.calls.catalog.presentation.history.toHistory
import com.nmichail.meridian.shared.busy.domain.usecase.LoadBusyPeopleUseCase
import com.nmichail.meridian.shared.session.domain.usecase.GetUserIdUseCase
import javax.inject.Inject

class CallsViewModel @Inject constructor(
	private val loadBusyPeopleUseCase: LoadBusyPeopleUseCase,
	private val loadCallsUseCase: LoadCallsUseCase,
	private val getUserIdUseCase: GetUserIdUseCase,
	private val loadPhotoUseCase: LoadPhotoUseCase,
	private val noteFacesUseCase: NoteFacesUseCase,
	private val errorDelegate: ErrorDelegate,
) : BaseViewModel<CallsState, CallsIntent, Nothing>() {

	override fun initState(): CallsState = CallsState.Initial

	override fun applyIntent(intent: CallsIntent) {
		when (intent) {
			is CallsIntent.LoadInitialData     -> handleLoadInitialData()
			is CallsIntent.OpenPage            -> handlePage(intent.page)
			is CallsIntent.OpenStart           -> handleScreen(CallsDestination.Start)
			is CallsIntent.OpenPlan            -> handleScreen(CallsDestination.Plan)
			is CallsIntent.OpenFind            -> handleFind()
			is CallsIntent.OpenMeeting         -> handleMeeting(intent)
			is CallsIntent.ToggleMeetingCamera -> handleToggleMeetingCamera()
			is CallsIntent.ShowMeeting         -> handleShowMeeting(intent.visible)
			is CallsIntent.PickDay             -> handleLoad(intent.index)
			is CallsIntent.Back                -> handleBack()
		}
	}

	private fun handleLoadInitialData() {
		if (state.value != CallsState.Initial) {
			return
		}
		handleLoadHistory()
	}

	private fun handlePage(next: CallPage) {
		val current = state.value as? CallsState.Content ?: return
		if (current.page == next) {
			return
		}
		setState(current.copy(page = next))
	}

	private fun handleScreen(screen: CallsDestination) {
		val current = state.value as? CallsState.Content ?: return
		setState(current.copy(screen = screen))
	}

	private fun handleMeeting(intent: CallsIntent.OpenMeeting) {
		val current = state.value as? CallsState.Content ?: return
		setState(
			current.copy(
				screen = CallsDestination.Meeting,
				title = intent.title.trim(),
				profilePhoto = null,
				meetingVisible = true,
				cameraEnabled = true,
			),
		)
		launchTry {
			val userId = getUserIdUseCase()?.takeIf(String::isNotBlank) ?: return@launchTry
			val photo = loadPhotoUseCase(userId) ?: return@launchTry
			val content = state.value as? CallsState.Content ?: return@launchTry
			if (content.screen == CallsDestination.Meeting) {
				setState(content.copy(profilePhoto = photo))
			}
		} catch errorDelegate::processNonBlockingError
	}

	private fun handleShowMeeting(visible: Boolean) {
		val content = state.value as? CallsState.Content ?: return
		if (content.screen == CallsDestination.Meeting) {
			setState(content.copy(meetingVisible = visible))
		}
	}

	private fun handleToggleMeetingCamera() {
		val content = state.value as? CallsState.Content ?: return
		if (content.screen == CallsDestination.Meeting) {
			setState(content.copy(cameraEnabled = !content.cameraEnabled))
		}
	}

	private fun handleFind() {
		val current = state.value as? CallsState.Content ?: return
		setState(
			current.copy(
				screen = CallsDestination.Find,
				people = null,
			),
		)
		handleLoad(current.day)
	}

	private fun handleBack() {
		val current = state.value as? CallsState.Content ?: return
		if (current.screen == CallsDestination.Find) {
			setState(current.copy(screen = CallsDestination.Plan))
			return
		}
		setState(current.copy(screen = CallsDestination.List))
		handleLoadHistory()
	}

	private fun handleLoad(day: Int) {
		if (day !in 0 until PLAN_DAYS) {
			return
		}
		val current = state.value as? CallsState.Content ?: return
		setState(current.copy(day = day, people = null))

		launchTry {
			val people = loadBusyPeopleUseCase(planDate(day))
			val content = state.value as? CallsState.Content ?: return@launchTry
			if (content.day != day) {
				return@launchTry
			}

			setState(content.copy(people = people))
		} catch { error ->
			val content = state.value as? CallsState.Content ?: return@catch
			if (content.day != day) {
				return@catch
			}

			errorDelegate.processNonBlockingError(error)
		}
	}

	private fun handleLoadHistory() {
		if (state.value is CallsState.Initial) {
			setState(CallsState.Loading)
		}
		launchTry {
			val currentUserId = getUserIdUseCase().orEmpty()
			val calls = loadCallsUseCase().map { record -> record.toHistory(currentUserId) }
			when (val current = state.value) {
				is CallsState.Content -> setState(current.copy(calls = calls))
				else                  -> setState(
					CallsState.Content(
						page = CallPage.Conferences,
						calls = calls,
						screen = CallsDestination.List,
						title = "",
						day = 0,
						people = null,
						profilePhoto = null,
						meetingVisible = false,
						cameraEnabled = true,
					),
				)
			}
			noteFacesUseCase(calls.map { call -> call.personId })
		} catch { error ->
			if (state.value is CallsState.Content) {
				errorDelegate.processNonBlockingError(error)
				return@catch
			}
			setState(CallsState.Error(message = error.userMessage()))
		}
	}
}