package com.nmichail.meridian.features.profile.about.presentation.compose

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.profile.about.R
import com.nmichail.meridian.features.profile.about.presentation.AboutIntent

@Composable
fun AboutScreen(onIntent: (AboutIntent) -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		AboutBar(onIntent)

		Column(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.padding(horizontal = 32.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			AboutIcon()

			Text(
				text = stringResource(R.string.about_name),
				style = MeridianType.titleScreen,
				color = palette.textPrimary,
				modifier = Modifier.padding(top = 20.dp),
			)

			Text(
				text = stringResource(R.string.about_description),
				style = MeridianType.bodyRegular,
				color = palette.textSecondary,
				textAlign = TextAlign.Center,
				modifier = Modifier.padding(top = 12.dp),
			)
		}

		Text(
			text = stringResource(R.string.about_version, appVersion()),
			style = MeridianType.bodySmall,
			color = palette.textSecondary,
			textAlign = TextAlign.Center,
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					start = 24.dp,
					end = 24.dp,
					top = 8.dp,
					bottom = 28.dp,
				),
		)
	}
}

@Composable
private fun AboutBar(onIntent: (AboutIntent) -> Unit) {
	Image(
		painter = painterResource(UiR.drawable.ic_back),
		contentDescription = null,
		colorFilter = ColorFilter.tint(palette.textPrimary),
		modifier = Modifier
			.padding(start = 16.dp, top = 14.dp)
			.size(16.dp)
			.clickable { onIntent(AboutIntent.Back) },
	)
}

@Composable
private fun AboutIcon() {
	val context = LocalContext.current
	val image = remember(context) { context.applicationIcon() }

	Image(
		bitmap = image,
		contentDescription = stringResource(R.string.about_name),
		modifier = Modifier
			.size(176.dp)
			.clip(RoundedCornerShape(40.dp)),
	)
}

@Composable
private fun appVersion(): String {
	val context = LocalContext.current

	return remember(context) { context.packageVersion() }
}

private fun Context.packageVersion(): String {
	val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
	} else {
		@Suppress("DEPRECATION")
		packageManager.getPackageInfo(packageName, 0)
	}

	return info.versionName.orEmpty()
}

private fun Context.applicationIcon() = packageManager
	.getApplicationIcon(packageName)
	.toBitmap(width = 704, height = 704, config = Bitmap.Config.ARGB_8888)
	.asImageBitmap()