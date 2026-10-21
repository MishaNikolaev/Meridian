package com.nmichail.meridian.features.profile.presentation.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.features.profile.R
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.text.MUiTitleScreenText

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		MUiTitleScreenText(text = stringResource(R.string.profile_title))
		MUiButton(
			model = MUiButtonModel(style = MUiButtonStyle.Danger, label = stringResource(R.string.profile_logout)),
			onClick = onLogout,
			modifier = Modifier
				.padding(top = 24.dp)
				.fillMaxWidth(),
		)
	}
}
