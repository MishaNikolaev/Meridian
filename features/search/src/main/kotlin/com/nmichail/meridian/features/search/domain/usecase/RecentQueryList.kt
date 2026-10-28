package com.nmichail.meridian.features.search.domain.usecase

internal fun String?.queries(): List<String> =
	orEmpty()
		.split("\n")
		.filter { query -> query.isNotEmpty() }
