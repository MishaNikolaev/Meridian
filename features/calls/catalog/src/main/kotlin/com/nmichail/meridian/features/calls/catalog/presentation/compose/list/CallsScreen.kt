package com.nmichail.meridian.features.calls.catalog.presentation.compose.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.nmichail.meridian.component.ui.tab.MUiTab
import com.nmichail.meridian.component.ui.tab.model.MUiTabModel
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.calls.catalog.R
import com.nmichail.meridian.features.calls.catalog.domain.entity.CallDirection
import com.nmichail.meridian.features.calls.catalog.presentation.CallPage
import com.nmichail.meridian.features.calls.catalog.presentation.CallsDestination
import com.nmichail.meridian.features.calls.catalog.presentation.compose.conference.CallStage
import com.nmichail.meridian.features.calls.catalog.presentation.compose.conference.FindTime
import com.nmichail.meridian.features.calls.catalog.presentation.compose.conference.LiveCall
import com.nmichail.meridian.features.calls.catalog.presentation.compose.conference.PlanConference
import com.nmichail.meridian.features.calls.catalog.presentation.compose.conference.StartConference
import com.nmichail.meridian.features.calls.catalog.presentation.history.HistoryCall
import com.nmichail.meridian.features.calls.catalog.presentation.CallsIntent
import com.nmichail.meridian.features.calls.catalog.presentation.CallsState
import kotlinx.coroutines.flow.drop

@Composable
fun CallsScreen(
	state: CallsState,
	onIntent: (CallsIntent) -> Unit,
) {
	LaunchedEffect(Unit) {
		onIntent(CallsIntent.LoadInitialData)
	}
	when (state) {
		CallsState.Initial, CallsState.Loading -> Unit
		is CallsState.Error -> Text(
			text = stringResource(state.message),
			style = MeridianType.bodyDefault,
			color = palette.textSecondary,
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
		)
		is CallsState.Content -> CallsContent(
			state = state,
			onIntent = onIntent,
		)
	}
}

@Composable
private fun CallsContent(
	state: CallsState.Content,
	onIntent: (CallsIntent) -> Unit,
) {
	when (state.screen) {
		CallsDestination.Start -> StartConference(
			onBack = { onIntent(CallsIntent.Back) },
			onStart = { title -> onIntent(CallsIntent.OpenMeeting(title = title)) },
		)
		CallsDestination.Plan -> PlanConference(
			onBack = { onIntent(CallsIntent.Back) },
			onFind = { onIntent(CallsIntent.OpenFind) },
		)
		CallsDestination.Find -> FindTime(
			day = state.day,
			people = state.people,
			onDay = { onIntent(CallsIntent.PickDay(it)) },
			onBack = { onIntent(CallsIntent.Back) },
		)
		CallsDestination.Solo -> CallStage(
			kind = LiveCall.Solo,
			title = state.title,
			onClose = { onIntent(CallsIntent.Back) },
			selfName = "",
			profilePhoto = "",
			cameraEnabled = null,
			onToggleCamera = {},
		)
		CallsDestination.Meeting -> {
			DisposableEffect(Unit) {
				onIntent(CallsIntent.ShowMeeting(visible = true))
				onDispose { onIntent(CallsIntent.ShowMeeting(visible = false)) }
			}
			CallStage(
				kind = LiveCall.Meeting,
				title = state.title,
				onClose = { onIntent(CallsIntent.Back) },
				selfName = state.profilePhoto?.name.orEmpty(),
				profilePhoto = state.profilePhoto?.photoId.orEmpty(),
				cameraEnabled = state.cameraEnabled,
				onToggleCamera = { onIntent(CallsIntent.ToggleMeetingCamera) },
			)
		}
		CallsDestination.List -> CallHome(
			page = state.page,
			calls = state.calls,
			onPage = { onIntent(CallsIntent.OpenPage(it)) },
			onStart = { onIntent(CallsIntent.OpenStart) },
			onPlan = { onIntent(CallsIntent.OpenPlan) },
			onJoin = { onIntent(CallsIntent.OpenMeeting(title = "")) },
		)
	}
}

@Composable
private fun CallHome(
	page: CallPage,
	calls: List<HistoryCall>,
	onPage: (CallPage) -> Unit,
	onStart: () -> Unit,
	onPlan: () -> Unit,
	onJoin: () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.canvas),
	) {
		val pagerState = rememberPagerState(
			initialPage = page.ordinal,
			pageCount = { CallPage.entries.size },
		)
		LaunchedEffect(page) {
			val index = page.ordinal
			if (pagerState.currentPage != index) {
				pagerState.animateScrollToPage(index)
			}
		}
		LaunchedEffect(pagerState) {
			snapshotFlow { pagerState.settledPage }.drop(1).collect { index ->
				onPage(CallPage.entries[index])
			}
		}
		Text(
			text = stringResource(R.string.calls_title),
			style = MeridianType.titleCard,
			color = palette.textPrimary,
			modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 4.dp),
		)
		CallTabs(pagerState = pagerState, onPage = onPage)
		HorizontalPager(
			state = pagerState,
			modifier = Modifier.weight(1f),
		) { index ->
			Column(
				modifier = Modifier
					.fillMaxSize()
					.verticalScroll(rememberScrollState()),
			) {
				when (CallPage.entries[index]) {
					CallPage.Conferences -> ConferencePage(onStart = onStart, onPlan = onPlan, onJoin = onJoin)
					CallPage.History -> HistoryPage(calls)
					CallPage.Missed -> HistoryPage(
						calls.filter { call -> call.direction == CallDirection.Missed },
					)
				}
			}
		}
	}
}

@Composable
private fun CallTabs(pagerState: PagerState, onPage: (CallPage) -> Unit) {
	val border = palette.borderDefault
	val position = pagerState.currentPage + pagerState.currentPageOffsetFraction
	Layout(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 5.dp)
			.drawBehind {
				val stroke = 1.dp.toPx()
				drawLine(border, Offset(0f, size.height), Offset(size.width, size.height), stroke)
			}
			.padding(horizontal = 6.dp),
		content = {
			CallPage.entries.forEach { item ->
				MUiTab(
					model = MUiTabModel(
						label = stringResource(item.label()),
						selected = item.ordinal == pagerState.currentPage,
					),
					indicator = false,
					onClick = { onPage(item) },
				)
			}
			Box(
				modifier = Modifier
					.height(3.dp)
					.clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
					.background(palette.textPrimary),
			)
		},
	) { measurables, constraints ->
		placeCallTabs(measurables, constraints, position)
	}
}

private fun MeasureScope.placeCallTabs(
	measurables: List<Measurable>,
	constraints: Constraints,
	position: Float,
): MeasureResult {
	val gap = 10.dp.roundToPx()
	val inset = 10.dp.roundToPx()
	val barHeight = 3.dp.roundToPx()
	val below = 5.dp.roundToPx()
	val loose = constraints.copy(minWidth = 0, minHeight = 0, maxWidth = Constraints.Infinity)
	val tabs = measurables.dropLast(1).map { measurable -> measurable.measure(loose) }
	val lefts = IntArray(tabs.size)
	var cursor = 0
	tabs.forEachIndexed { index, tab ->
		lefts[index] = cursor
		cursor += tab.width + gap
	}
	val span = position.coerceIn(0f, tabs.lastIndex.toFloat())
	val index = span.toInt().coerceIn(0, tabs.lastIndex)
	val next = (index + 1).coerceAtMost(tabs.lastIndex)
	val fraction = span - index
	val barLeft = lerp(lefts[index] + inset, lefts[next] + inset, fraction)
	val barWidth = lerp(tabs[index].width - inset * 2, tabs[next].width - inset * 2, fraction)
	val bar = measurables.last().measure(Constraints.fixed(barWidth.coerceAtLeast(0), barHeight))
	val height = tabs.maxOf { tab -> tab.height }
	return layout(constraints.maxWidth, height) {
		tabs.forEachIndexed { tabIndex, tab -> tab.place(lefts[tabIndex], 0) }
		bar.place(barLeft, height - below - barHeight)
	}
}

private fun CallPage.label(): Int = when (this) {
	CallPage.Conferences -> R.string.calls_tab_meetings
	CallPage.History -> R.string.calls_tab_calls
	CallPage.Missed -> R.string.calls_tab_missed
}
