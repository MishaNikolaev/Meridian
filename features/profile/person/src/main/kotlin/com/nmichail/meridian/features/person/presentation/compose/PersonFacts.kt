package com.nmichail.meridian.features.person.presentation.compose

import android.content.ClipData
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.text.MUiLinkedText
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select
import com.nmichail.meridian.features.person.R
import com.nmichail.meridian.features.person.presentation.PersonIntent
import com.nmichail.meridian.features.person.presentation.PersonState

@Composable
internal fun PersonFacts(card: PersonState.Content, onIntent: (PersonIntent) -> Unit) {
	Column(
		modifier = Modifier.padding(top = if (card.corporate) 14.dp else 5.dp),
	) {
		if (card.corporate) {
			Text(
				text = stringResource(R.string.person_company),
				modifier = Modifier.padding(bottom = 14.dp),
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
			PersonWork(card, onIntent)
			PersonContacts(card)
		}
	}
}

@Composable
private fun PersonWork(card: PersonState.Content, onIntent: (PersonIntent) -> Unit) {
	if (card.corporate && card.server.isNotEmpty()) {
		PersonRow(
			R.drawable.ic_person_server, R.string.person_server,
			card.server,
			link = false,
			copy = false,
			onClick = null
		)
	}

	if (card.department.isNotEmpty()) {
		PersonRow(
			UiR.drawable.ic_building, R.string.person_department,
			card.department,
			link = false,
			copy = false,
			onClick = null
		)
	}

	if (card.office.isNotEmpty()) {
		PersonRow(
			R.drawable.ic_person_office, R.string.person_office,
			card.office,
			link = false,
			copy = false,
			onClick = null
		)
	}

	if (card.manager.isNotEmpty()) {
		PersonRow(
			icon = R.drawable.ic_person_user,
			label = R.string.person_manager,
			value = card.manager,
			link = card.managerId.isNotEmpty(),
			copy = false,
			onClick = if (card.managerId.isEmpty()) {
				null
			} else {
				{ onIntent(PersonIntent.OpenManager) }
			},
		)
	}
}

@Composable
private fun PersonContacts(card: PersonState.Content) {
	if (card.position.isNotEmpty()) {
		PersonRow(
			R.drawable.ic_person_case, R.string.person_position,
			card.position,
			link = false,
			copy = false,
			onClick = null
		)
	}

	if (card.email.isNotEmpty()) {
		PersonRow(
			UiR.drawable.ic_mail, R.string.person_email,
			card.email,
			link = true,
			copy = false,
			mail = true,
			onClick = null
		)
	}

	if (card.phone.isNotEmpty()) {
		PersonRow(
			R.drawable.ic_person_phone, R.string.person_phone,
			card.phone,
			link = false,
			copy = true,
			onClick = null
		)
	}
}

@Composable
private fun PersonRow(
	icon: Int,
	label: Int,
	value: String,
	link: Boolean,
	copy: Boolean,
	onClick: (() -> Unit)?,
	mail: Boolean = false,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.then(
				if (onClick == null) {
					Modifier
				} else {
					Modifier.clickable(onClick = onClick)
				},
			)
			.padding(
				start = 16.dp,
				end = 14.dp,
				top = 12.dp,
				bottom = 12.dp,
			),
		horizontalArrangement = Arrangement.spacedBy(14.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			painter = painterResource(icon),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textSecondary),
			modifier = Modifier.size(22.dp),
		)

		PersonValue(
			label = label,
			value = value,
			link = link,
			mail = mail,
			modifier = Modifier.weight(1f),
		)

		if (copy) {
			CopyValue(value)
		}
	}
}

@Composable
private fun PersonValue(
	label: Int,
	value: String,
	link: Boolean,
	mail: Boolean,
	modifier: Modifier,
) {
	val clipboard = LocalClipboard.current

	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(2.dp),
	) {
		if (mail) {
			MUiLinkedText(
				text = value,
				style = MeridianType.bodyDefault,
				textColor = palette.accentPrimary,
				onLinkLongClick = {
					clipboard.nativeClipboard.setPrimaryClip(
						ClipData.newPlainText("text", it),
					)
				},
			)
		} else {
			Text(
				text = value,
				style = MeridianType.bodyDefault,
				color = select(
					active = link,
					on = palette.accentPrimary,
					off = palette.textPrimary,
				),
			)
		}

		Text(
			text = stringResource(label),
			style = MeridianType.captionSmall,
			color = palette.textSecondary,
		)
	}
}

@Composable
private fun CopyValue(value: String) {
	val clipboard = LocalClipboard.current

	Image(
		painter = painterResource(R.drawable.ic_person_copy),
		contentDescription = null,
		colorFilter = ColorFilter.tint(palette.accentPrimary),
		modifier = Modifier
			.size(20.dp)
			.clickable {
				clipboard.nativeClipboard.setPrimaryClip(
					ClipData.newPlainText("text", value),
				)
			},
	)
}
