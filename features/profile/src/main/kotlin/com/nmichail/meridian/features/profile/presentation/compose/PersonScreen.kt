package com.nmichail.meridian.features.profile.presentation.compose

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.text.MUiLinkedText
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.profile.R
import com.nmichail.meridian.features.profile.presentation.PersonIntent
import com.nmichail.meridian.features.profile.presentation.PersonState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun PersonScreen(
	userId: String,
	corporate: Boolean,
	state: StateFlow<PersonState>,
	onIntent: (PersonIntent) -> Unit,
) {
	val uiState by state.collectAsStateWithLifecycle()
	LaunchedEffect(Unit) {
		onIntent(
			PersonIntent.Load(
				userId = userId,
				corporate = corporate,
			),
		)
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

			is PersonState.Error -> Text(
				text = stringResource(current.message),
				style = MeridianType.bodyRegular,
				color = palette.textSecondary,
				modifier = Modifier.align(Alignment.Center),
			)

			is PersonState.Content -> PersonContent(
				card = current,
				onIntent = onIntent,
			)
		}
	}
}

@Composable
private fun PersonContent(
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
			modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			PersonIdentity(card)
			PersonActions(onIntent)
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
			modifier = Modifier
				.size(24.dp)
				.clickable { onIntent(PersonIntent.Back) },
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(R.drawable.ic_person_back),
				contentDescription = null,
				modifier = Modifier.size(16.dp),
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
				modifier = Modifier.size(width = 4.dp, height = 15.dp),
			)
		}
	}
}

@Composable
private fun PersonIdentity(card: PersonState.Content) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		PersonMark(
			name = card.name,
			corporate = card.corporate,
		)
		Text(
			text = card.name,
			style = MeridianType.titleSection,
			color = palette.textPrimary,
			textAlign = TextAlign.Center,
		)
		if (card.position.isNotEmpty()) {
			Text(
				text = card.position,
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
private fun PersonMark(name: String, corporate: Boolean) {
	val parts = name.trim().split(" ").filter { it.isNotEmpty() }
	val letters = if (parts.size >= 2) {
		parts[0].take(1) + parts[1].take(1)
	} else {
		name.take(2)
	}
	val fill = if (corporate) palette.accentPrimarySoft else palette.accentExternal
	val ink = if (corporate) palette.accentPrimary else palette.textOnAccent
	Box(
		modifier = Modifier
			.size(96.dp)
			.clip(CircleShape)
			.background(fill),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = letters.uppercase(),
			style = MeridianType.titleSection.copy(
				fontWeight = FontWeight.SemiBold,
				fontSize = 35.sp,
				lineHeight = 40.sp,
			),
			color = ink,
		)
	}
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
private fun PersonActions(onIntent: (PersonIntent) -> Unit) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		PersonButton(
			icon = R.drawable.ic_person_forum,
			label = R.string.person_write,
			filled = true,
			onClick = { onIntent(PersonIntent.Write) },
			modifier = Modifier.weight(1f),
		)
		PersonButton(
			icon = R.drawable.ic_person_phone,
			label = R.string.person_call,
			filled = false,
			onClick = null,
			modifier = Modifier.weight(1f),
		)
		PersonButton(
			icon = R.drawable.ic_person_video,
			label = R.string.person_video,
			filled = false,
			onClick = null,
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
	val tint = if (filled) palette.textOnAccent else palette.accentPrimary
	val ink = if (filled) palette.textOnAccent else palette.textPrimary
	Column(
		modifier = modifier
			.clip(RoundedCornerShape(16.dp))
			.background(if (filled) palette.accentButton else palette.surface)
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

@Composable
private fun PersonFacts(card: PersonState.Content, onIntent: (PersonIntent) -> Unit) {
	Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
		if (card.corporate) {
			Text(
				text = stringResource(R.string.person_company),
				style = MeridianType.bodyName,
				color = palette.textPrimary,
			)
		}
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(20.dp))
				.background(palette.surface),
		) {
			if (card.corporate && card.server.isNotEmpty()) {
				PersonRow(R.drawable.ic_person_server, R.string.person_server, card.server, link = false, copy = false, onClick = null)
			}
			if (card.department.isNotEmpty()) {
				PersonRow(UiR.drawable.ic_building, R.string.person_department, card.department, link = false, copy = false, onClick = null)
			}
			if (card.office.isNotEmpty()) {
				PersonRow(R.drawable.ic_person_office, R.string.person_office, card.office, link = false, copy = false, onClick = null)
			}
			if (card.manager.isNotEmpty()) {
				PersonRow(
					icon = null,
					label = R.string.person_manager,
					value = card.manager,
					link = card.managerId.isNotEmpty(),
					copy = false,
					portrait = R.drawable.manager_photo,
					onClick = if (card.managerId.isEmpty()) {
						null
					} else {
						{ onIntent(PersonIntent.OpenManager) }
					},
				)
			}
			if (card.position.isNotEmpty()) {
				PersonRow(R.drawable.ic_person_case, R.string.person_position, card.position, link = false, copy = false, onClick = null)
			}
			if (card.email.isNotEmpty()) {
				PersonRow(UiR.drawable.ic_mail, R.string.person_email, card.email, link = true, copy = false, mail = true, onClick = null)
			}
			if (card.phone.isNotEmpty()) {
				PersonRow(R.drawable.ic_person_phone, R.string.person_phone, card.phone, link = false, copy = true, onClick = null)
			}
		}
	}
}

@Composable
private fun PersonRow(
	icon: Int?,
	label: Int,
	value: String,
	link: Boolean,
	copy: Boolean,
	onClick: (() -> Unit)?,
	portrait: Int? = null,
	mail: Boolean = false,
) {
	val clipboard = LocalClipboardManager.current
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick))
			.padding(start = 16.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
		horizontalArrangement = Arrangement.spacedBy(14.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		if (portrait != null) {
			Image(
				painter = painterResource(portrait),
				contentDescription = null,
				modifier = Modifier.size(32.dp).clip(CircleShape),
			)
		} else if (icon == null) {
			Box(modifier = Modifier.size(22.dp))
		} else {
			Image(
				painter = painterResource(icon),
				contentDescription = null,
				colorFilter = ColorFilter.tint(palette.textSecondary),
				modifier = Modifier.size(22.dp),
			)
		}
		Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
			if (mail) {
				MUiLinkedText(
					text = value,
					style = MeridianType.bodyDefault,
					textColor = palette.accentPrimary,
					onLinkLongClick = { clipboard.setText(AnnotatedString(it)) },
				)
			} else {
				Text(
					text = value,
					style = MeridianType.bodyDefault,
					color = if (link) palette.accentPrimary else palette.textPrimary,
				)
			}
			Text(
				text = stringResource(label),
				style = MeridianType.captionSmall,
				color = palette.textSecondary,
			)
		}
		if (copy) {
			Image(
				painter = painterResource(R.drawable.ic_person_copy),
				contentDescription = null,
				colorFilter = ColorFilter.tint(palette.accentPrimary),
				modifier = Modifier
					.size(20.dp)
					.clickable { clipboard.setText(AnnotatedString(value)) },
			)
		}
	}
}
