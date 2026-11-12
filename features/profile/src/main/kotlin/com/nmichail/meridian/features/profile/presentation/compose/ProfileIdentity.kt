package com.nmichail.meridian.features.profile.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.photopicker.compose.PhotoMark
import com.nmichail.meridian.component.photopicker.compose.PhotoPicker
import com.nmichail.meridian.component.ui.field.MUiTextField
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.profile.R
import com.nmichail.meridian.features.profile.presentation.ProfileIntent

@Composable
internal fun IdentityCard(
	name: String,
	position: String,
	host: String,
	status: String,
	draft: String?,
	photo: String,
	installing: Boolean,
	onIntent: (ProfileIntent) -> Unit,
) {
	var picking by remember { mutableStateOf(false) }

	val openPicker = {
		if (!installing) {
			picking = true
		}
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(22.dp))
			.background(palette.surface)
			.padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(14.dp),
	) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(14.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			PhotoMark(
				name = name,
				photoId = photo,
				enabled = !installing,
				onOpen = openPicker,
			)

			Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
				Text(
					text = name,
					style = MeridianType.titleCard,
					color = palette.textPrimary,
				)

				if (position.isNotEmpty()) {
					Text(
						text = position,
						style = MeridianType.bodySmall,
						color = palette.textSecondary,
						modifier = Modifier.padding(top = 4.dp),
					)
				}

				if (host.isNotBlank()) {
					ConnectedHost(host = host)
				}
			}
		}

		StatusEditor(
			status = status,
			draft = draft,
			onIntent = onIntent,
		)
	}

	PhotoPicker(
		open = picking,
		onDismiss = { picking = false },
		onPhoto = { bytes, type ->
			onIntent(ProfileIntent.InstallPhoto(bytes = bytes, type = type))
		},
	)
}

@Composable
private fun ConnectedHost(host: String) {
	Row(
		modifier = Modifier.padding(top = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(6.dp),
	) {
		Box(
			modifier = Modifier
				.size(8.dp)
				.clip(CircleShape)
				.background(palette.statusSuccess),
		)

		Text(
			text = stringResource(R.string.profile_connected, host),
			style = MeridianType.captionDefault,
			color = palette.statusSuccess,
		)
	}
}

@Composable
private fun StatusEditor(
	status: String,
	draft: String?,
	onIntent: (ProfileIntent) -> Unit,
) {
	if (draft == null) {
		StatusButton(
			text = status.ifEmpty { stringResource(R.string.profile_set_status) },
			icon = status.isEmpty(),
			onClick = { onIntent(ProfileIntent.StartStatus) },
		)

		return
	}

	val save = { onIntent(ProfileIntent.SaveStatus) }

	Row(
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.None,
				value = draft,
				placeholder = stringResource(R.string.profile_set_status),
			),
			onValueChange = { value -> onIntent(ProfileIntent.ChangeStatus(value = value)) },
			onDone = save,
			modifier = Modifier.weight(1f),
		)

		StatusCheck(onClick = save)
	}
}

@Composable
private fun StatusCheck(onClick: () -> Unit) {
	Box(
		modifier = Modifier
			.size(32.dp)
			.clickable(onClick = onClick),
		contentAlignment = Alignment.Center,
	) {
		Image(
			painter = painterResource(R.drawable.ic_profile_check),
			contentDescription = stringResource(R.string.profile_save_status),
			colorFilter = ColorFilter.tint(palette.textPrimary),
			modifier = Modifier.size(22.dp),
		)
	}
}

@Composable
private fun StatusButton(
	text: String,
	icon: Boolean,
	onClick: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick)
			.border(width = 1.dp,
				color = palette.borderDefault,
				shape = RoundedCornerShape(14.dp)
			)
			.clip(RoundedCornerShape(14.dp))
			.background(palette.field)
			.padding(horizontal = 14.dp, vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(10.dp),
	) {
		if (icon) {
			Image(
				painter = painterResource(R.drawable.ic_profile_smile),
				contentDescription = null,
				colorFilter = ColorFilter.tint(palette.textPrimary),
				modifier = Modifier.size(22.dp),
			)
		}

		Text(
			text = text,
			style = MeridianType.bodyDefault,
			color = palette.textPrimary,
		)
	}
}