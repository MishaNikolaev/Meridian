package com.nmichail.meridian.shared.chat.data.message

private const val FIELD = "\u001f"

internal fun noticeText(raw: String): String? {
	val parts = raw.split(FIELD)

	val actor = parts.getOrNull(1)?.takeIf { name -> name.isNotEmpty() } ?: return null

	val target = parts.getOrNull(2).orEmpty()

	return when (parts.firstOrNull()) {
		"added" -> "$actor добавил $target"
		"removed" -> "$actor удалил $target"
		"left" -> "$actor покинул чат"
		"admin" -> "$actor сделал админом $target"
		else -> null
	}
}