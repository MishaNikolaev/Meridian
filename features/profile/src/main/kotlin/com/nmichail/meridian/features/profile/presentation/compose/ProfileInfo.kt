package com.nmichail.meridian.features.profile.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.profile.R

@Composable
internal fun CorporateFacts(
	host: String,
	department: String,
	position: String,
	email: String,
) {
	InfoBlock(
		title = stringResource(R.string.profile_corporate),
		facts = buildList {
			if (host.isNotBlank()) {
				add(ProfileFact(R.drawable.ic_profile_server, host, R.string.profile_server))
			}

			if (department.isNotEmpty()) {
				add(ProfileFact(R.drawable.ic_profile_people, department, R.string.profile_department))
			}

			if (position.isNotEmpty()) {
				add(ProfileFact(R.drawable.ic_profile_work, position, R.string.profile_position))
			}

			if (email.isNotEmpty()) {
				add(ProfileFact(R.drawable.ic_profile_mail, email, R.string.profile_email))
			}
		},
	)
}

@Composable
internal fun InfoBlock(
	title: String,
	facts: List<ProfileFact>,
) {
	if (facts.isEmpty()) {
		return
	}

	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		SectionTitle(title)

		Column(
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(20.dp))
				.background(palette.surface)
				.padding(vertical = 4.dp),
		) {
			facts.forEachIndexed { index, fact ->
				InfoLine(fact = fact)

				if (index != facts.lastIndex) {
					BoxDivider(start = 44.dp)
				}
			}
		}
	}
}

@Composable
private fun InfoLine(fact: ProfileFact) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 12.dp, vertical = 7.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Image(
			painter = painterResource(fact.icon),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textPrimary),
			modifier = Modifier.size(20.dp),
		)

		Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
			Text(
				text = fact.value,
				style = MeridianType.bodyDefault,
				color = palette.textPrimary,
			)

			Text(
				text = stringResource(fact.label),
				style = MeridianType.captionSmall,
				color = palette.textSecondary,
			)
		}
	}
}

@Composable
internal fun MoreBlock(
	version: String,
	theme: String,
	onAppearance: () -> Unit,
) {
	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		SectionTitle(stringResource(R.string.profile_more))

		Column(
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(20.dp))
				.background(palette.surface)
				.padding(vertical = 4.dp),
		) {
			MoreLine(
				icon = R.drawable.ic_profile_palette,
				title = R.string.profile_appearance,
				value = theme,
				onClick = onAppearance,
			)

			BoxDivider(start = 58.dp)

			MoreLine(
				icon = R.drawable.ic_profile_info,
				title = R.string.profile_about,
				value = version,
			)
		}
	}
}

@Composable
private fun SectionTitle(title: String) {
	Text(
		text = title,
		style = MeridianType.bodySmall.copy(fontWeight = FontWeight.SemiBold),
		color = palette.textSecondary,
	)
}

@Composable
private fun MoreLine(
	icon: Int,
	title: Int,
	value: String,
	onClick: (() -> Unit)? = null,
) {
	val action = if (onClick == null) {
		Modifier
	} else {
		Modifier.clickable(onClick = onClick)
	}

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.then(action)
			.padding(horizontal = 12.dp, vertical = 6.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(10.dp),
	) {
		Box(
			modifier = Modifier.size(36.dp),
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(icon),
				contentDescription = null,
				colorFilter = ColorFilter.tint(palette.textPrimary),
				modifier = Modifier.size(22.dp),
			)
		}

		Text(
			text = stringResource(title),
			style = MeridianType.bodyDefault,
			color = palette.textPrimary,
			modifier = Modifier.weight(1f),
		)

		Text(
			text = value,
			style = MeridianType.captionDefault,
			color = palette.textSecondary,
		)

		Image(
			painter = painterResource(UiR.drawable.ic_chevron_right),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textSecondary),
			modifier = Modifier.size(18.dp),
		)
	}
}

@Composable
private fun BoxDivider(start: Dp) {
	Box(
		modifier = Modifier
			.padding(start = start, end = 12.dp)
			.fillMaxWidth()
			.height(1.dp)
			.background(palette.borderDefault),
	)
}