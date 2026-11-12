package com.nmichail.meridian.features.chat.presentation.compose.component

import androidx.compose.ui.graphics.Color
import com.nmichail.meridian.features.chat.R

internal data class CatalogChannel(
	val initials: Int,
	val title: Int,
	val preview: Int,
	val joined: Boolean,
	val time: Int?,
	val start: Color,
	val end: Color,
)

internal val CATALOG = listOf(
	CatalogChannel(
		initials = R.string.catalog_info_initials,
		title = R.string.catalog_info_title,
		preview = R.string.catalog_info_preview,
		joined = false,
		time = null,
		start = Color(color = 0xFF947115),
		end = Color(color = 0xFFB65410),
	),
	CatalogChannel(
		initials = R.string.catalog_news_initials,
		title = R.string.catalog_news_title,
		preview = R.string.catalog_news_preview,
		joined = true,
		time = R.string.chat_yesterday,
		start = Color(color = 0xFF4678B6),
		end = Color(color = 0xFF2563EB),
	),
	CatalogChannel(
		initials = R.string.catalog_release_initials,
		title = R.string.catalog_release_title,
		preview = R.string.catalog_release_preview,
		joined = false,
		time = null,
		start = Color(color = 0xFF7A65B6),
		end = Color(color = 0xFF7C3AED),
	),
	CatalogChannel(
		initials = R.string.catalog_support_initials,
		title = R.string.catalog_support_title,
		preview = R.string.catalog_support_preview,
		joined = false,
		time = null,
		start = Color(color = 0xFF1B7D71),
		end = Color(color = 0xFF0F766E),
	),
	CatalogChannel(
		initials = R.string.catalog_knowledge_initials,
		title = R.string.catalog_knowledge_title,
		preview = R.string.catalog_knowledge_preview,
		joined = false,
		time = null,
		start = Color(color = 0xFF475569),
		end = Color(color = 0xFF0F172A),
	),
	CatalogChannel(
		initials = R.string.catalog_hr_initials,
		title = R.string.catalog_hr_title,
		preview = R.string.catalog_hr_preview,
		joined = false,
		time = null,
		start = Color(color = 0xFFB25385),
		end = Color(color = 0xFFE11D48),
	),
)