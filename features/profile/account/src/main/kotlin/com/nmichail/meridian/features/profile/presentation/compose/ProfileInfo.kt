package com.nmichail.meridian.features.profile.presentation.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.nmichail.meridian.component.ui.theme.ThemeDialog
import com.nmichail.meridian.component.ui.theme.label
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.themeChoice
import com.nmichail.meridian.features.profile.R

@Composable
internal fun CorporateFacts(
	host: String,
	department: String,
	position: String,
	email: String,
) {
	val lines = listOfNotNull(
		host.takeIf { value -> value.isNotBlank() }?.let { value ->
			Triple(R.drawable.ic_profile_server, value, R.string.profile_server)
		},
		department.takeIf { value -> value.isNotEmpty() }?.let { value ->
			Triple(R.drawable.ic_profile_people, value, R.string.profile_department)
		},
		position.takeIf { value -> value.isNotEmpty() }?.let { value ->
			Triple(R.drawable.ic_profile_work, value, R.string.profile_position)
		},
		email.takeIf { value -> value.isNotEmpty() }?.let { value ->
			Triple(R.drawable.ic_profile_mail, value, R.string.profile_email)
		},
	)

	if (lines.isEmpty()) {
		return
	}

	Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
		SectionTitle(stringResource(R.string.profile_corporate))

		Column(
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(20.dp))
				.background(palette.surface)
				.padding(vertical = 4.dp),
		) {
			lines.forEachIndexed { index, line ->
				InfoLine(icon = line.first, value = line.second, label = line.third)

				if (index != lines.lastIndex) {
					BoxDivider(start = 44.dp)
				}
			}
		}
	}
}

@Composable
private fun InfoLine(
	icon: Int,
	value: String,
	label: Int,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 12.dp, vertical = 7.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Image(
			painter = painterResource(icon),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textPrimary),
			modifier = Modifier.size(20.dp),
		)

		Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
			Text(
				text = value,
				style = MeridianType.bodyDefault,
				color = palette.textPrimary,
			)

			Text(
				text = stringResource(label),
				style = MeridianType.captionSmall,
				color = palette.textSecondary,
			)
		}
	}
}

@Composable
internal fun AppearanceBlock(
	version: String,
	onPlan: () -> Unit,
	onAbout: () -> Unit,
) {
	var open by remember { mutableStateOf(false) }
	val choice = themeChoice()

	MoreBlock(
		version = version,
		theme = stringResource(choice.label()),
		onAppearance = { open = true },
		onPlan = onPlan,
		onAbout = onAbout,
	)

	ThemeDialog(
		open = open,
		onDismiss = { open = false },
	)
}

@Composable
internal fun MoreBlock(
	version: String,
	theme: String,
	onAppearance: () -> Unit,
	onPlan: () -> Unit,
	onAbout: () -> Unit,
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
				icon = R.drawable.ic_profile_calendar,
				title = R.string.profile_plan,
				onClick = onPlan,
			)

			BoxDivider(start = 58.dp)

			MoreLine(
				icon = R.drawable.ic_profile_info,
				title = R.string.profile_about,
				value = version,
				onClick = onAbout,
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
	value: String? = null,
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

		if (!value.isNullOrBlank()) {
			Text(
				text = value,
				style = MeridianType.captionDefault,
				color = palette.textSecondary,
			)
		}

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