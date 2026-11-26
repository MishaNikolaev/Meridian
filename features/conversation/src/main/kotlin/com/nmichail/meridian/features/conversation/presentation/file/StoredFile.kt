package com.nmichail.meridian.features.conversation.presentation.file

private const val FILE_MARK = "+file:"

fun String.markedFile(bytes: Int): String = "$this$FILE_MARK$bytes"

fun String.asFile(): Boolean = contains(FILE_MARK)

fun String.mediaType(): String = substringBefore(FILE_MARK)

fun String.storedBytes(): Long = substringAfter(FILE_MARK, "").toLongOrNull() ?: 0L
