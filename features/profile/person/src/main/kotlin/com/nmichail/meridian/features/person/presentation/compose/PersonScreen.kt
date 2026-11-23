package com.nmichail.meridian.features.person.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmichail.meridian.component.photo.PhotoFace
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.effect.LiveDataEffect
import com.nmichail.meridian.component.ui.error.MUiErrorInPlace
import com.nmichail.meridian.component.ui.snackbar.MUiSnackbarHost
import com.nmichail.meridian.component.ui.snackbar.showErrorSnackBar
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select
import com.nmichail.meridian.features.person.R
import com.nmichail.meridian.features.person.presentation.PersonEvent
import com.nmichail.meridian.features.person.presentation.PersonIntent
import com.nmichail.meridian.features.person.presentation.PersonState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PersonScreen(
	userId: String,
	corporate: Boolean,
	state: StateFlow<PersonState>,
	event: LiveData<PersonEvent>,
	onIntent: (PersonIntent) -> Unit,
) {
	val uiState by state.collectAsStateWithLifecycle()
	val snackBarHostState = remember { SnackbarHostState() }
	val context = LocalContext.current

	LaunchedEffect(Unit) {
		onIntent(
			PersonIntent.Load(
				userId = userId,
				corporate = corporate,
			),
		)
	}

	LiveDataEffect(event) { value ->
		if (value is PersonEvent.CannotCallSelf) {
			showErrorSnackBar(
				snackBarHostState = snackBarHostState,
				message = context.getString(R.string.person_call_self),
			)
		}
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		when (val current = uiState) {
			PersonState.Initial,
			PersonState.Loading,
								   -> Unit

			is PersonState.Error   -> MUiErrorInPlace(
				message = stringResource(current.message),
				retry = stringResource(UiR.string.error_retry),
				back = stringResource(UiR.string.error_back),
				onRetry = {
					onIntent(
						PersonIntent.Load(
							userId = userId,
							corporate = corporate,
						),
					)
				},
				onBack = { onIntent(PersonIntent.Back) },
				modifier = Modifier.align(Alignment.Center),
			)

			is PersonState.Content -> PersonContent(
				userId = userId,
				card = current,
				onIntent = onIntent,
			)
		}
		MUiSnackbarHost(
			snackBarHostState = snackBarHostState,
			modifier = Modifier.align(Alignment.BottomCenter),
		)
	}
}

@Composable
private fun PersonContent(
	userId: String,
	card: PersonState.Content,
	onIntent: (PersonIntent) -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.verticalScroll(rememberScrollState()),
	) {
		PersonToolbar(onIntent)

		Column(
			modifier = Modifier.padding(
				start = 16.dp,
				end = 16.dp,
				bottom = 16.dp
			),
		) {
			PersonIdentity(userId = userId, card = card)

			PersonActions(userId, onIntent)

			val filled = listOf(
				card.department,
				card.office,
				card.manager,
				card.position,
				card.email,
				card.phone,
			).any { value -> value.isNotEmpty() } || (card.corporate && card.server.isNotEmpty())

			if (filled) {
				PersonFacts(card, onIntent)
			}
		}
	}
}

@Composable
private fun PersonToolbar(onIntent: (PersonIntent) -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 8.dp, vertical = 6.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Box(
			modifier = Modifier.size(24.dp),
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(R.drawable.ic_person_back),
				contentDescription = null,
				colorFilter = ColorFilter.tint(palette.textPrimary),
				modifier = Modifier
					.size(16.dp)
					.clickable { onIntent(PersonIntent.Back) },
			)
		}

		Box(modifier = Modifier.weight(1f))

		Box(
			modifier = Modifier.size(24.dp),
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(R.drawable.ic_person_more),
				contentDescription = null,
				colorFilter = ColorFilter.tint(palette.textPrimary),
				modifier = Modifier.size(width = 4.dp, height = 15.dp),
			)
		}
	}
}

@Composable
private fun PersonIdentity(userId: String, card: PersonState.Content) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		PersonMark(name = card.name, userId = userId, photo = card.photo)

		Text(
			text = card.name,
			style = MeridianType.titleSection,
			color = palette.textPrimary,
			textAlign = TextAlign.Center,
		)

		if (card.position.isNotEmpty()) {
			Text(
				text = card.position,
				modifier = Modifier.padding(vertical = 5.dp),
				style = MeridianType.bodySmall,
				color = palette.textSecondary,
				textAlign = TextAlign.Center,
			)
		}

		PersonStatus(
			status = card.status,
			corporate = card.corporate,
		)
	}
}

@Composable
private fun PersonMark(name: String, userId: String, photo: String) {
	PhotoFace(
		name = name,
		userId = userId,
		photoId = photo,
		size = 96.dp,
		style = MeridianType.titleSection.copy(
			fontWeight = FontWeight.SemiBold,
			fontSize = 35.sp,
			lineHeight = 40.sp,
		),
	)
}

@Composable
private fun PersonStatus(status: String, corporate: Boolean) {
	if (status.isEmpty() && corporate) {
		return
	}

	Row(
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		if (status.isNotEmpty()) {
			Row(
				modifier = Modifier
					.clip(RoundedCornerShape(14.dp))
					.background(palette.surface)
					.padding(start = 10.dp, end = 12.dp, top = 5.dp, bottom = 5.dp),
				horizontalArrangement = Arrangement.spacedBy(6.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Box(
					modifier = Modifier
						.size(8.dp)
						.clip(CircleShape)
						.background(palette.statusOnline),
				)

				Text(
					text = status,
					style = MeridianType.captionDefault,
					color = palette.textPrimary,
				)
			}
		}

		if (!corporate) {
			Text(
				text = stringResource(R.string.person_external),
				style = MeridianType.captionDefault.copy(fontWeight = FontWeight.SemiBold),
				color = palette.accentExternal,
				modifier = Modifier
					.clip(RoundedCornerShape(14.dp))
					.background(palette.accentExternalSoft)
					.padding(horizontal = 10.dp, vertical = 5.dp),
			)
		}
	}
}

@Composable
private fun PersonActions(userId: String, onIntent: (PersonIntent) -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 12.dp),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		PersonButton(
			icon = R.drawable.ic_person_forum,
			label = R.string.person_write,
			filled = true,
			onClick = { onIntent(PersonIntent.Write(userId)) },
			modifier = Modifier.weight(1f),
		)

		PersonButton(
			icon = R.drawable.ic_person_phone,
			label = R.string.person_call,
			filled = false,
			onClick = { onIntent(PersonIntent.Call(userId)) },
			modifier = Modifier.weight(1f),
		)

		PersonButton(
			icon = R.drawable.ic_person_video,
			label = R.string.person_video,
			filled = false,
			onClick = { onIntent(PersonIntent.Video(userId)) },
			modifier = Modifier.weight(1f),
		)
	}
}

@Composable
private fun PersonButton(
	icon: Int,
	label: Int,
	filled: Boolean,
	onClick: (() -> Unit)?,
	modifier: Modifier = Modifier,
) {
	val tint = select(
		active = filled,
		on = palette.textOnAccent,
		off = palette.accentPrimary,
	)

	val ink = select(
		active = filled,
		on = palette.textOnAccent,
		off = palette.textPrimary,
	)

	Column(
		modifier = modifier
			.clip(RoundedCornerShape(16.dp))
			.background(
				select(
					active = filled,
					on = palette.accentButton,
					off = palette.surface,
				),
			)
			.then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick))
			.padding(top = 12.dp, bottom = 10.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		Image(
			painter = painterResource(icon),
			contentDescription = null,
			colorFilter = ColorFilter.tint(tint),
			modifier = Modifier.size(22.dp),
		)

		Text(
			text = stringResource(label),
			style = MeridianType.captionSmall,
			color = ink,
		)
	}
}
