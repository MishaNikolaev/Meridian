package com.nmichail.meridian.features.chat.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.field.MUiTextField
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.ui.text.MUiTitleCardText
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.core.validation.username.UsernameValidationItem
import com.nmichail.meridian.features.chat.R
import com.nmichail.meridian.features.chat.presentation.ChatSection

@Composable
fun CreateChatSheet(
	corporate: Boolean,
	section: ChatSection,
	title: DisplayNameValidationItem,
	username: UsernameValidationItem,
	onTitle: (String) -> Unit,
	onUsername: (String) -> Unit,
	onCreate: () -> Unit,
) {
	Column(
		modifier = Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(16.dp)).background(MeridianThemeTokens.palette.surface).padding(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		MUiTitleCardText(
			text = stringResource(
				if (section == ChatSection.WORK) {
					R.string.chat_create_group
				} else if (corporate) {
					R.string.chat_create_colleague
				} else {
					R.string.chat_create_direct
				},
			),
		)
		when (section) {
			ChatSection.WORK -> {
				MUiTextField(
					model = MUiTextFieldModel(
						icon = MUiFieldIcon.None, value = title.data,
						placeholder = stringResource(R.string.chat_create_title), error = titleError(title),
					),
					onValueChange = onTitle,
				)
				MUiTextField(
					model = MUiTextFieldModel(
						icon = MUiFieldIcon.None, value = username.data,
						placeholder = stringResource(R.string.chat_member_login), error = usernameError(username),
					),
					onValueChange = onUsername,
				)
			}
			ChatSection.ALL,
			ChatSection.PERSONAL,
			ChatSection.BOTS -> MUiTextField(
				model = MUiTextFieldModel(
					icon = MUiFieldIcon.None, value = username.data,
					placeholder = stringResource(
						if (corporate) {
							R.string.chat_login_colleague
						} else {
							R.string.chat_login
						},
					),
					error = usernameError(username),
				),
				onValueChange = onUsername,
			)
		}
		MUiButton(
			model = MUiButtonModel(style = MUiButtonStyle.Primary, label = stringResource(R.string.chat_create)),
			onClick = onCreate,
			modifier = Modifier.fillMaxWidth(),
		)
	}
}
