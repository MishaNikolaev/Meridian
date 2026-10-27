package com.nmichail.meridian.component.ui.text

import android.text.SpannableString
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.text.getSpans
import com.nmichail.meridian.component.ui.theme.palette

@Composable
fun MUiLinkedText(
	text: String,
	style: TextStyle,
	modifier: Modifier = Modifier,
	linkText: String? = null,
	textColor: Color = Color.Unspecified,
	linkColor: Color = palette.accentPrimary,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
	onTextLayout: ((TextLayoutResult) -> Unit)? = null,
	onLinkClick: (String) -> Unit = {},
	onLinkLongClick: (String) -> Unit = {},
) {
	var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
	val currentOnLinkClick by rememberUpdatedState(onLinkClick)
	val currentOnLinkLongClick by rememberUpdatedState(onLinkLongClick)
	val linkStyles = TextLinkStyles(
		style = SpanStyle(
			color = linkColor,
			textDecoration = TextDecoration.Underline,
		),
	)
	val annotatedString = rememberLinkedString(
		text = text,
		linkText = linkText,
		linkStyles = linkStyles,
		onLinkClick = currentOnLinkClick,
	)
	Text(
		text = annotatedString,
		style = style,
		color = textColor,
		maxLines = maxLines,
		overflow = overflow,
		modifier = modifier.pointerInput(annotatedString) {
			detectTapGestures(
				onLongPress = { offset ->
					val position = textLayoutResult?.getOffsetForPosition(offset) ?: return@detectTapGestures
					val address = annotatedString.linkTagAt(position) ?: return@detectTapGestures
					currentOnLinkLongClick(address)
				},
			)
		},
		onTextLayout = { layout ->
			textLayoutResult = layout
			onTextLayout?.invoke(layout)
		},
	)
}

@Composable
private fun rememberLinkedString(
	text: String,
	linkText: String?,
	linkStyles: TextLinkStyles,
	onLinkClick: (String) -> Unit,
): AnnotatedString = remember(text, linkText, linkStyles) {
	if (linkText != null) {
		linkedPhrase(text, linkText, linkStyles, onLinkClick)
	} else {
		linkedEmails(text, linkStyles, onLinkClick)
	}
}

private fun linkedEmails(
	text: String,
	linkStyles: TextLinkStyles,
	onLinkClick: (String) -> Unit,
): AnnotatedString = AnnotatedString.Builder(text).apply {
	val spannable = SpannableString(text).apply { Linkify.addLinks(this, Linkify.EMAIL_ADDRESSES) }
	spannable.getSpans<URLSpan>().forEach { span ->
		val address = span.url.removePrefix("mailto:")
		addLink(
			clickable = LinkAnnotation.Clickable(
				tag = address,
				styles = linkStyles,
				linkInteractionListener = { onLinkClick(address) },
			),
			start = spannable.getSpanStart(span),
			end = spannable.getSpanEnd(span),
		)
	}
}.toAnnotatedString()

private fun linkedPhrase(
	text: String,
	linkText: String,
	linkStyles: TextLinkStyles,
	onLinkClick: (String) -> Unit,
): AnnotatedString = AnnotatedString.Builder(text).apply {
	val start = text.indexOf(linkText, ignoreCase = true)
	if (start < 0) {
		return@apply
	}
	addLink(
		clickable = LinkAnnotation.Clickable(
			tag = linkText,
			styles = linkStyles,
			linkInteractionListener = { onLinkClick(linkText) },
		),
		start = start,
		end = start + linkText.length,
	)
}.toAnnotatedString()

private fun AnnotatedString.linkTagAt(position: Int): String? =
	getLinkAnnotations(position, position)
		.firstOrNull()
		?.item
		?.let { it as? LinkAnnotation.Clickable }
		?.tag
