package com.nmichail.meridian.shared.chat.domain.entity

data class ReactionMemory(
	val chosen: Map<String, Set<String>>,
	val used: List<String>,
)