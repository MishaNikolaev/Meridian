package com.nmichail.meridian.features.conversation.presentation.compose.attach

import android.Manifest
import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import androidx.core.content.FileProvider
import com.nmichail.meridian.features.conversation.R
import com.nmichail.meridian.features.conversation.presentation.compose.poll.rememberSheetHeight
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun AttachSheet(
	onDismiss: () -> Unit,
	onFile: (PickedFile) -> Unit,
	onPhotos: (List<PickedFile>, String) -> Unit,
	onPoll: () -> Unit,
) {
	val context = LocalContext.current
	val view = LocalView.current
	val focusManager = LocalFocusManager.current
	val keyboard = LocalSoftwareKeyboardController.current
	var photos by remember { mutableStateOf<List<RecentFile>>(emptyList()) }
	var docs by remember { mutableStateOf<List<RecentFile>>(emptyList()) }
	var chosen by remember { mutableStateOf<List<android.net.Uri>>(emptyList()) }
	val actions = rememberAttachActions(
		onFile = onFile,
		onChosen = onPhotos,
		onPhotos = { photos = it },
		onDocs = { docs = it },
	)

	LaunchedEffect(Unit) {
		focusManager.clearFocus(force = true)
		keyboard?.hide()
		val hidden = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
		val mode = hidden or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
		(view.parent as? DialogWindowProvider)?.window?.setSoftInputMode(mode)
		if (actions.allowed(context)) {
			actions.reload()
		} else {
			actions.ask()
		}
	}

	Dialog(
		onDismissRequest = onDismiss,
		properties = DialogProperties(
			usePlatformDefaultWidth = false,
			decorFitsSystemWindows = false,
			dismissOnClickOutside = false,
		),
	) {
		AttachPanel(
			photos = photos,
			docs = docs,
			chosen = chosen,
			onDismiss = onDismiss,
			onToggle = { uri ->
				chosen = when {
					uri in chosen -> chosen.filterNot { item -> item == uri }
					chosen.size >= 10 -> chosen
					else -> chosen + uri
				}
			},
			onPhoto = actions.open,
			onCamera = actions.camera,
			onBrowse = actions.browse,
			onImages = actions.images,
			onAllow = actions.ask,
			onSend = { text -> actions.send(chosen, text) },
			onPoll = {
				onDismiss()
				onPoll()
			},
		)
	}
}

@Composable
@Suppress("MagicNumber")
private fun AttachPanel(
	photos: List<RecentFile>,
	docs: List<RecentFile>,
	chosen: List<android.net.Uri>,
	onDismiss: () -> Unit,
	onToggle: (android.net.Uri) -> Unit,
	onPhoto: (android.net.Uri) -> Unit,
	onCamera: () -> Unit,
	onBrowse: () -> Unit,
	onImages: () -> Unit,
	onAllow: () -> Unit,
	onSend: (String) -> Unit,
	onPoll: () -> Unit,
) {
	var tab by remember { mutableStateOf(AttachTab.Gallery) }
	val sheet = rememberSheetHeight()
	val scrim = remember { MutableInteractionSource() }
	val captioning = tab == AttachTab.Gallery && chosen.isNotEmpty()

	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.BottomCenter,
	) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.clickable(interactionSource = scrim, indication = null, onClick = onDismiss),
		)
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.fillMaxHeight(sheet.fraction)
				.then(if (captioning) Modifier.imePadding() else Modifier)
				.clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
				.background(palette.page)
				.focusProperties { canFocus = false }
				.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
		) {
			when (tab) {
				AttachTab.Gallery -> GalleryGrid(photos, chosen, onToggle, onCamera, onAllow, Modifier.fillMaxSize())
				AttachTab.Files -> FilePlaces(docs, onBrowse, onImages, onPhoto, onAllow, sheet, Modifier.fillMaxSize())
				AttachTab.Poll -> Unit
			}
			if (captioning) {
				CaptionBar(
					onSend = onSend,
					modifier = Modifier.align(Alignment.BottomCenter),
				)
			} else {
				SheetTabs(
					tab = tab,
					onTab = { tab = it },
					onPoll = onPoll,
					shown = sheet.fraction < 1f,
					modifier = Modifier.align(Alignment.BottomCenter),
				)
			}
		}
	}
}

@Composable
private fun GalleryGrid(
	photos: List<RecentFile>,
	chosen: List<android.net.Uri>,
	onToggle: (android.net.Uri) -> Unit,
	onCamera: () -> Unit,
	onAllow: () -> Unit,
	modifier: Modifier = Modifier,
) {
	if (photos.isEmpty()) {
		AllowLine(onAllow = onAllow, modifier = modifier)
		return
	}

	val bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 88.dp

	LazyVerticalGrid(
		columns = GridCells.Fixed(count = 3),
		modifier = modifier.fillMaxWidth(),
		contentPadding = PaddingValues(start = 8.dp, top = 8.dp, end = 8.dp, bottom = bottom),
		horizontalArrangement = Arrangement.spacedBy(4.dp),
		verticalArrangement = Arrangement.spacedBy(4.dp),
	) {
		item {
			Box(
				modifier = Modifier
					.aspectRatio(1f)
					.clip(RoundedCornerShape(8.dp))
					.background(palette.textPrimary)
					.clickable(onClick = onCamera),
				contentAlignment = Alignment.Center,
			) {
				Image(
					painter = painterResource(R.drawable.ic_camera),
					contentDescription = null,
					modifier = Modifier.size(28.dp),
				)
			}
		}
		items(items = photos, key = { file -> file.uri.toString() }) { file ->
			MarkedPhoto(
				uri = file.uri,
				name = file.name,
				video = file.video,
				marked = file.uri in chosen,
				onClick = { onToggle(file.uri) },
			)
		}
	}
}

@Composable
private fun AllowLine(onAllow: () -> Unit, modifier: Modifier = Modifier) {
	Text(
		text = stringResource(R.string.conversation_attach_allow),
		style = MeridianType.bodyDefault,
		color = palette.accentPrimary,
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = onAllow)
			.padding(24.dp),
	)
}

@Composable
private fun SheetTabs(
	tab: AttachTab,
	onTab: (AttachTab) -> Unit,
	onPoll: () -> Unit,
	shown: Boolean,
	modifier: Modifier = Modifier,
) {
	if (!shown) {
		return
	}

	Row(
		modifier = modifier
			.navigationBarsPadding()
			.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp)
			.clip(RoundedCornerShape(22.dp))
			.background(palette.surface.copy(alpha = 0.75f))
			.padding(horizontal = 8.dp, vertical = 6.dp),
		horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.CenterHorizontally),
		verticalAlignment = Alignment.CenterVertically,
	) {
		TabLabel(
			icon = R.drawable.ic_gallery,
			text = stringResource(R.string.conversation_attach_gallery),
			selected = tab == AttachTab.Gallery,
			onClick = { onTab(AttachTab.Gallery) },
		)
		TabLabel(
			icon = R.drawable.ic_file,
			text = stringResource(R.string.conversation_attach_file),
			selected = tab == AttachTab.Files,
			onClick = { onTab(AttachTab.Files) },
		)
		TabLabel(
			icon = R.drawable.ic_poll,
			text = stringResource(R.string.conversation_attach_poll),
			selected = tab == AttachTab.Poll,
			onClick = onPoll,
		)
	}
}

@Composable
private fun CaptionBar(onSend: (String) -> Unit, modifier: Modifier = Modifier) {
	var text by remember { mutableStateOf("") }

	Row(
		modifier = modifier
			.navigationBarsPadding()
			.padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
			.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp),
	) {
		BasicTextField(
			value = text,
			onValueChange = { text = it },
			singleLine = true,
			textStyle = MeridianType.bodyDefault.copy(color = palette.textPrimary),
			modifier = Modifier
				.weight(1f)
				.clip(RoundedCornerShape(22.dp))
				.background(palette.surface.copy(alpha = 0.92f))
				.padding(horizontal = 16.dp, vertical = 12.dp),
			decorationBox = { inner ->
				Box {
					if (text.isEmpty()) {
						Text(
							text = stringResource(R.string.conversation_message),
							style = MeridianType.bodyDefault,
							color = palette.textSecondary,
						)
					}
					inner()
				}
			},
		)
		Box(
			modifier = Modifier
				.size(44.dp)
				.clip(CircleShape)
				.background(palette.accentPrimary)
				.clickable { onSend(text.trim()) },
			contentAlignment = Alignment.Center,
		) {
			Image(
				painter = painterResource(R.drawable.ic_send),
				contentDescription = null,
				colorFilter = ColorFilter.tint(Color.White),
				modifier = Modifier.size(width = 16.dp, height = 14.dp),
			)
		}
	}
}

@Composable
private fun TabLabel(icon: Int, text: String, selected: Boolean, onClick: () -> Unit) {
	val tint = if (selected) palette.accentPrimary else palette.textSecondary

	Column(
		modifier = Modifier
			.clip(RoundedCornerShape(16.dp))
			.background(if (selected) palette.accentPrimarySoft.copy(alpha = 0.75f) else Color.Transparent)
			.clickable(onClick = onClick)
			.padding(horizontal = 14.dp, vertical = 8.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Image(
			painter = painterResource(icon),
			contentDescription = null,
			colorFilter = ColorFilter.tint(tint),
			modifier = Modifier.size(22.dp),
		)
		Text(
			text = text,
			style = MeridianType.chatMeta,
			color = tint,
			modifier = Modifier.padding(top = 4.dp),
		)
	}
}

@Composable
private fun rememberAttachActions(
	onFile: (PickedFile) -> Unit,
	onChosen: (List<PickedFile>, String) -> Unit,
	onPhotos: (List<RecentFile>) -> Unit,
	onDocs: (List<RecentFile>) -> Unit,
): AttachActions {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()
	val permissions = remember { mediaPermissions() }
	val ask = rememberLauncherForActivityResult(RequestMultiplePermissions()) { answer ->
		if (answer.values.any { granted -> granted }) {
			scope.launch { onPhotos(withContext(Dispatchers.IO) { context.recentFiles(images = true) }) }
			scope.launch { onDocs(withContext(Dispatchers.IO) { context.recentFiles(images = false) }) }
		}
	}
	val storage = rememberStorage(onFile)
	val cameraFile = remember {
		val file = File(File(context.cacheDir, "files").apply { mkdirs() }, "camera.jpg").apply { createNewFile() }
		FileProvider.getUriForFile(context, "${context.packageName}.files", file)
	}
	val camera = rememberLauncherForActivityResult(TakePicture()) { saved ->
		if (!saved) return@rememberLauncherForActivityResult
		scope.launch {
			val picked = withContext(Dispatchers.IO) { context.readPickedFile(cameraFile) } ?: return@launch
			onFile(picked.copy(name = "photo.jpg", type = "image/jpeg"))
		}
	}

	return AttachActions(
		ask = { ask.launch(permissions) },
		reload = {
			scope.launch { onPhotos(withContext(Dispatchers.IO) { context.recentFiles(images = true) }) }
			scope.launch { onDocs(withContext(Dispatchers.IO) { context.recentFiles(images = false) }) }
		},
		camera = { camera.launch(cameraFile) },
		browse = storage.first,
		images = storage.second,
		open = { uri ->
			scope.launch {
				val picked = withContext(Dispatchers.IO) { context.readPickedFile(uri) } ?: return@launch
				onFile(picked.copy(document = true))
			}
		},
		send = { uris, caption ->
			scope.launch {
				val picked = withContext(Dispatchers.IO) { uris.mapNotNull { uri -> context.readPickedFile(uri) } }
				if (picked.isNotEmpty()) {
					onChosen(picked, caption)
				}
			}
		},
		allowed = { host ->
			permissions.any { permission ->
				host.checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
			}
		},
	)
}

private fun mediaPermissions(): Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
	val visual = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
		arrayOf(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
	} else {
		emptyArray()
	}
	arrayOf(
		Manifest.permission.READ_MEDIA_IMAGES,
		Manifest.permission.READ_MEDIA_VIDEO,
		Manifest.permission.READ_MEDIA_AUDIO,
	) + visual
} else {
	arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}