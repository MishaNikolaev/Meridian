package com.nmichail.meridian.features.authorization.splash.presentation.compose

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmichail.meridian.component.ui.text.MUiBaseText
import com.nmichail.meridian.component.ui.theme.Glow
import com.nmichail.meridian.component.ui.theme.Glow2
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.authorization.splash.R
import com.nmichail.meridian.features.authorization.splash.presentation.SplashIntent

@Composable
fun SplashScreen(
	onIntent: (SplashIntent) -> Unit,
) {
	LaunchedEffect(Unit) {
		onIntent(SplashIntent.LoadInitialData)
	}

	Box(modifier = Modifier.fillMaxSize()) {
		SplashBackground()

		SplashGlow(
			color = Glow,
			alpha = palette.splashGlowAlpha,
			modifier = Modifier
				.align(Alignment.TopCenter)
				.offset(y = 80.dp)
				.size(600.dp),
		)

		SplashGlow(
			color = Glow2,
			alpha = palette.splashGlow2Alpha,
			modifier = Modifier
				.align(Alignment.TopCenter)
				.offset(x = 90.dp, y = (-110).dp)
				.size(380.dp),
		)

		SplashIdentity(
			modifier = Modifier.align(Alignment.Center),
		)

		MUiBaseText(
			text = stringResource(R.string.splash_version,
				LocalContext.current.appVersion()
			),
			style = MeridianType.captionSmall,
			color = palette.splashVersion,
			textAlign = TextAlign.Center,
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.navigationBarsPadding()
				.padding(bottom = 20.dp),
		)
	}
}

@Composable
private fun SplashBackground() {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(
				brush = Brush.verticalGradient(
					colors = listOf(palette.splashGradientTop, palette.splashGradientBottom),
				),
			),
	)
}

@Composable
private fun SplashGlow(
	color: Color,
	alpha: Float,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier.background(
			brush = Brush.radialGradient(
				colorStops = arrayOf(
					Pair(first = 0.7f, second = color.copy(alpha = alpha)),
					Pair(first = 1f, second = Color.Transparent),
				),
			),
		),
	)
}

@Composable
private fun SplashIdentity(
	modifier: Modifier = Modifier,
) {
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		SplashMark()

		Spacer(modifier = Modifier.height(30.dp))

		MUiBaseText(
			text = stringResource(R.string.splash_title),
			style = MeridianType.displayAuth.copy(
				fontSize = 34.sp,
				lineHeight = 41.sp,
			),
			color = palette.splashTitle,
			textAlign = TextAlign.Center,
		)

		Spacer(modifier = Modifier.height(8.dp))

		MUiBaseText(
			text = stringResource(R.string.splash_subtitle),
			style = MeridianType.bodyRegular,
			color = palette.splashSubtitle,
			textAlign = TextAlign.Center,
		)
	}
}

@Composable
private fun SplashMark() {
	val shape = RoundedCornerShape(42.dp)

	Box(
		modifier = Modifier
			.size(132.dp)
			.clip(shape)
			.background(color = palette.splashIcon)
			.border(
				width = 1.dp,
				color = palette.splashIconBorder,
				shape = shape,
			),
		contentAlignment = Alignment.Center,
	) {
		Image(
			painter = painterResource(R.drawable.ic_splash_mark),
			contentDescription = null,
			colorFilter = ColorFilter.tint(palette.splashMark),
			modifier = Modifier.size(width = 81.dp, height = 61.dp),
		)
	}
}

private fun Context.appVersion(): String {
	val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		packageManager.getPackageInfo(
			packageName,
			PackageManager.PackageInfoFlags.of(0),
		)
	} else {
		@Suppress("DEPRECATION")
		packageManager.getPackageInfo(packageName, 0)
	}

	return info.versionName.orEmpty()
}