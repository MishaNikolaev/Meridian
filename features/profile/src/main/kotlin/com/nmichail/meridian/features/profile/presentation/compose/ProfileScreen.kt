package com.nmichail.meridian.features.profile.presentation.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.text.MUiTitleScreenText
import com.nmichail.meridian.features.profile.presentation.ProfileViewModel

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		MUiTitleScreenText(text = "Профиль")
		MUiButton(
			model = MUiButtonModel(style = MUiButtonStyle.Danger, label = "Выйти"),
			onClick = viewModel::logout,
			modifier = Modifier
				.padding(top = 24.dp)
				.fillMaxWidth(),
		)
	}
}
