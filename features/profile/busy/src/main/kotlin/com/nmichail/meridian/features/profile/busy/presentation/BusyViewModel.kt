package com.nmichail.meridian.features.profile.busy.presentation

import com.nmichail.meridian.core.network.error.presentation.ErrorDelegate
import com.nmichail.meridian.core.network.error.userMessage
import com.nmichail.meridian.core.presentation.BaseViewModel
import com.nmichail.meridian.shared.busy.domain.usecase.LoadOwnBusyUseCase
import com.nmichail.meridian.shared.busy.domain.usecase.SaveBusySlotsUseCase
import com.nmichail.meridian.features.profile.busy.presentation.compose.PLAN_DAYS
import com.nmichail.meridian.features.profile.busy.presentation.compose.planDate
import com.nmichail.meridian.features.profile.busy.presentation.compose.planSlotLabels
import javax.inject.Inject

class BusyViewModel @Inject constructor(
	private val loadOwnBusyUseCase: LoadOwnBusyUseCase,
	private val saveBusySlotsUseCase: SaveBusySlotsUseCase,
	private val errorDelegate: ErrorDelegate,
	private val router: BusyRouter,
) : BaseViewModel<BusyState, BusyIntent, Nothing>() {

	override fun initState(): BusyState = BusyState.Initial

	override fun applyIntent(intent: BusyIntent) {
		when (intent) {
			is BusyIntent.Load    -> handleLoad(
				when (val current = state.value) {
					is BusyState.Content -> current.day
					is BusyState.Error   -> current.day
					is BusyState.Loading -> current.day
					BusyState.Initial    -> 0
				},
			)

			is BusyIntent.Back    -> handleBack()
			is BusyIntent.PickDay -> handlePickDay(intent.index)
			is BusyIntent.Toggle  -> handleToggle(intent.slot)
		}
	}

	private fun handleBack() {
		router.navigateBack()
	}

	private fun handlePickDay(index: Int) {
		val current = state.value as? BusyState.Content

		if (current?.updating == true || index !in 0 until PLAN_DAYS) {
			return
		}

		setState(BusyState.Loading(day = index))
		handleLoad(index)
	}

	private fun handleToggle(slot: Int) {
		val current = state.value as? BusyState.Content ?: return

		if (current.updating || slot !in planSlotLabels.indices) {
			return
		}

		val content = current.copy(
			slots = if (slot in current.slots) {
				current.slots - slot
			} else {
				current.slots + slot
			},
			updating = true,
		)

		setState(content)

		launchTry {
			val slots = saveBusySlotsUseCase(planDate(content.day), content.slots.sorted())

			handleLoaded(content.day, slots)
		} catch { error -> handleFailure(error, content.day) }
	}

	private fun handleLoad(day: Int) {
		if (state.value !is BusyState.Content) {
			setState(BusyState.Loading(day = day))
		}

		launchTry {
			val slots = loadOwnBusyUseCase(planDate(day))

			handleLoaded(day, slots)
		} catch { error -> handleFailure(error, day) }
	}

	private fun handleLoaded(day: Int, slots: List<Int>) {
		val current = state.value

		val currentDay = when (current) {
			is BusyState.Content -> current.day
			is BusyState.Error   -> current.day
			is BusyState.Loading -> current.day
			BusyState.Initial    -> return
		}

		if (currentDay != day) {
			return
		}

		setState(
			BusyState.Content(
				day = day,
				slots = slots.toSet(),
				updating = false
			)
		)
	}

	private fun handleFailure(error: Exception, day: Int) {
		val current = state.value

		val currentDay = when (current) {
			is BusyState.Content -> current.day
			is BusyState.Error   -> current.day
			is BusyState.Loading -> current.day
			BusyState.Initial    -> return
		}

		if (currentDay != day) {
			return
		}

		if (current is BusyState.Content) {
			setState(current.copy(updating = false))
			errorDelegate.processNonBlockingError(error)
			return
		}

		setState(BusyState.Error(day = day, message = error.userMessage()))
	}
}