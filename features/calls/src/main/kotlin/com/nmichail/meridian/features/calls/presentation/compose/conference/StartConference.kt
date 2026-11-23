package com.nmichail.meridian.features.calls.presentation.compose.conference

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.control.MUiSwitch
import com.nmichail.meridian.component.ui.control.model.MUiSwitchModel
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.calls.R

@Composable
internal fun StartConference(onBack: () -> Unit, onStart: (String) -> Unit) {
	var lobby by remember { mutableStateOf(true) }
	var title by remember { mutableStateOf("") }
	val code = remember { inviteCode() }
	val link = stringResource(R.string.calls_start_link, code)
	val clipboard = LocalClipboardManager.current

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		FormBar(stringResource(R.string.calls_start_title), onBack)
		Column(
			modifier = Modifier
				.weight(1f)
				.padding(horizontal = 16.dp, vertical = 12.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp),
		) {
			FormCard {
				FormName(value = title, onValue = { title = it })
				FormLine(
					icon = R.drawable.ic_call_link,
					caption = stringResource(R.string.calls_link),
					value = link,
					trailing = R.drawable.ic_call_copy,
					onTrailing = { clipboard.setText(AnnotatedString(link)) },
				)
				FormLine(R.drawable.ic_call_person_add, stringResource(R.string.calls_invite), stringResource(R.string.calls_invite_hint), UiR.drawable.ic_chevron_right)
			}
			FormCard {
				SwitchLine(stringResource(R.string.calls_lobby), stringResource(R.string.calls_lobby_hint), lobby) { lobby = !lobby }
			}
		}
		FormButton(stringResource(R.string.calls_start_title), { onStart(title.trim()) }, inset = false)
	}
}

@Composable
private fun FormName(value: String, onValue: (String) -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 11.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Image(
			painter = painterResource(R.drawable.ic_call_edit),
			contentDescription = null,
			modifier = Modifier.size(22.dp),
		)
		Column(modifier = Modifier.weight(1f)) {
			Text(text = stringResource(R.string.calls_name), style = MeridianType.captionSmall, color = palette.textSecondary)
			BasicTextField(
				value = value,
				onValueChange = onValue,
				singleLine = true,
				textStyle = MeridianType.bodyDefault.copy(color = palette.textPrimary),
				cursorBrush = SolidColor(palette.accentPrimary),
				modifier = Modifier.fillMaxWidth(),
			)
		}
	}
}

@Composable
internal fun FormBar(title: String, onBack: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(4.dp),
	) {
		Image(
			painter = painterResource(UiR.drawable.ic_back),
			contentDescription = stringResource(R.string.calls_back),
			modifier = Modifier
				.size(16.dp)
				.clickable(onClick = onBack),
		)
		Text(text = title, style = MeridianType.titleCard, color = palette.textPrimary)
	}
}

@Composable
internal fun FormCard(content: @Composable () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(20.dp))
			.background(palette.surface)
			.padding(vertical = 4.dp),
		content = { content() },
	)
}

@Composable
internal fun FormLine(
	icon: Int,
	caption: String,
	value: String,
	trailing: Int? = null,
	onClick: (() -> Unit)? = null,
	onTrailing: (() -> Unit)? = null,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick))
			.padding(horizontal = 16.dp, vertical = 11.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(22.dp))
		Column(modifier = Modifier.weight(1f)) {
			Text(text = caption, style = MeridianType.captionSmall, color = palette.textSecondary)
			Text(text = value, style = MeridianType.bodyDefault, color = palette.textPrimary)
		}
		if (trailing != null) {
			Image(
				painter = painterResource(trailing),
				contentDescription = null,
				modifier = Modifier
					.size(24.dp)
					.then(if (onTrailing == null) Modifier else Modifier.clickable(onClick = onTrailing)),
			)
		}
	}
}

@Composable
internal fun SwitchLine(title: String, hint: String, checked: Boolean, onClick: () -> Unit) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 10.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Column(modifier = Modifier.weight(1f)) {
			Text(text = title, style = MeridianType.bodyDefault, color = palette.textPrimary)
			if (hint.isNotEmpty()) {
				Text(text = hint, style = MeridianType.captionSmall, color = palette.textSecondary)
			}
		}
		MUiSwitch(model = MUiSwitchModel(checked = checked), onClick = onClick)
	}
}

@Composable
internal fun FormButton(label: String, onClick: () -> Unit, inset: Boolean = true) {
	Text(
		text = label,
		style = MeridianType.bodyName,
		color = palette.textOnAccent,
		modifier = Modifier
			.fillMaxWidth()
			.then(if (inset) Modifier.navigationBarsPadding() else Modifier)
			.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = if (inset) 16.dp else 8.dp)
			.clip(RoundedCornerShape(16.dp))
			.background(palette.textPrimary)
			.clickable(onClick = onClick)
			.padding(vertical = 15.dp),
		textAlign = androidx.compose.ui.text.style.TextAlign.Center,
	)
}