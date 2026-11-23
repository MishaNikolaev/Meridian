package com.nmichail.meridian.features.calls.presentation.compose.media

import android.Manifest
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.webrtc.SurfaceViewRenderer

internal val LocalCallCapture = staticCompositionLocalOf<CallCapture?> { null }

@Composable
internal fun WithCallCamera(content: @Composable () -> Unit) {
	val context = LocalContext.current
	var allowed by remember { mutableStateOf(false) }
	val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { answer ->
		allowed = answer[Manifest.permission.CAMERA] == true &&
			answer[Manifest.permission.RECORD_AUDIO] == true
	}
	LaunchedEffect(Unit) {
		launcher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
	}
	val capture = remember(allowed) { if (allowed) CallCapture(context.applicationContext) else null }
	DisposableEffect(capture) {
		onDispose { capture?.stop() }
	}
	CompositionLocalProvider(LocalCallCapture provides capture, content = content)
}

@Composable
fun rememberCallLine(video: Boolean, active: Boolean = true): CallLine? {
	val context = LocalContext.current
	val micGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
	val cameraGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
	var allowed by remember { mutableStateOf(micGranted && (!video || cameraGranted)) }
	val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { answer ->
		val mic = answer[Manifest.permission.RECORD_AUDIO] == true
		val camera = !video || answer[Manifest.permission.CAMERA] == true
		allowed = mic && camera
	}
	LaunchedEffect(video, active) {
		if (!active) {
			return@LaunchedEffect
		}
		val permissions = if (video) {
			arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
		} else {
			arrayOf(Manifest.permission.RECORD_AUDIO)
		}
		launcher.launch(permissions)
	}
	val line = remember(allowed, video, active) {
		if (active && allowed) CallLine(context.applicationContext, video) else null
	}
	DisposableEffect(line) { onDispose { line?.stop() } }
	return line
}

@Composable
internal fun CallPicture(line: CallLine, remote: Boolean, modifier: Modifier = Modifier) {
	AndroidView(
		modifier = modifier,
		factory = { viewContext ->
			SurfaceViewRenderer(viewContext).apply {
				layoutParams = ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT,
				)
				line.show(this, remote)
			}
		},
		onRelease = { renderer -> line.hide(renderer) },
	)
}

@Composable
internal fun CallVideo(modifier: Modifier = Modifier) {
	val capture = LocalCallCapture.current ?: return
	AndroidView(
		modifier = modifier.fillMaxSize(),
		factory = { viewContext ->
			SurfaceViewRenderer(viewContext).apply {
				layoutParams = ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT,
				)
				capture.bind(this)
			}
		},
	)
}
