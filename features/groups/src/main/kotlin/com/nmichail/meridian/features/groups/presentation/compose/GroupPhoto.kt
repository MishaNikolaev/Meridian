package com.nmichail.meridian.features.groups.presentation.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import com.nmichail.meridian.component.photopicker.compose.PhotoFace
import com.nmichail.meridian.component.photopicker.compose.PhotoPicker
import com.nmichail.meridian.component.ui.initials.initials
import com.nmichail.meridian.component.ui.initials.markColor
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.component.ui.theme.select
import com.nmichail.meridian.features.groups.R

@Composable
internal fun GroupPhoto(
	title: String,
	photo: String,
	enabled: Boolean,
	onPhoto: (ByteArray, String) -> Unit,
) {
	var picking by remember { mutableStateOf(false) }

	val open = {
		if (enabled) {
			picking = true
		}
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(enabled = enabled, onClick = open),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(8.dp),
	) {
		Box(modifier = Modifier.size(96.dp)) {
			GroupMark(title = title, photo = photo)

			GroupBadge(modifier = Modifier.align(Alignment.BottomEnd))
		}

		Text(
			text = stringResource(R.string.group_change_photo),
			style = MeridianType.bodyDefault,
			color = palette.accentPrimary,
		)
	}

	PhotoPicker(
		open = picking,
		circle = false,
		onDismiss = { picking = false },
		onPhoto = onPhoto,
	)
}

@Composable
private fun GroupMark(title: String, photo: String) {
	if (photo.isNotEmpty()) {
		PhotoFace(
			name = title,
			photoId = photo,
			size = 96.dp,
			corner = 14.dp,
			style = MeridianType.titleCard,
		)

		return
	}

	Box(
		modifier = Modifier
			.size(96.dp)
			.clip(RoundedCornerShape(14.dp))
			.background(
				select(
					active = title.isBlank(),
					on = palette.accentPrimarySoft,
					off = markColor(title),
				),
			),
		contentAlignment = Alignment.Center,
	) {
		if (title.isBlank()) {
			Image(
				painter = painterResource(R.drawable.ic_group_camera),
				contentDescription = null,
				modifier = Modifier.size(28.dp),
			)
		} else {
			Text(
				text = initials(title),
				style = MeridianType.titleCard,
				color = palette.textOnAccent,
			)
		}
	}
}

@Composable
private fun GroupBadge(modifier: Modifier) {
	Box(
		modifier = modifier
			.offset(x = 2.dp, y = 2.dp)
			.size(32.dp)
			.border(width = 3.dp, color = palette.page, shape = RoundedCornerShape(16.dp))
			.clip(RoundedCornerShape(16.dp))
			.background(palette.accentButton),
		contentAlignment = Alignment.Center,
	) {
		Image(
			painter = painterResource(R.drawable.ic_group_camera),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.textOnAccent),
			modifier = Modifier.size(16.dp),
		)
	}
}