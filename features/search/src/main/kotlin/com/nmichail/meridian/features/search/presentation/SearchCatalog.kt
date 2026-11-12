package com.nmichail.meridian.features.search.presentation

//TODO Выпилить после фичи поиска
private val catalog = listOf(
	SearchHit.Person(
		title = "Власов Кирилл Вадимович",
		note = "Дизайнер · Отдел продуктового дизайна",
	),
	SearchHit.Person(
		title = "Анна Соколова",
		note = "Дизайнер · Отдел продуктового дизайна",
	),
	SearchHit.Person(
		title = "Игорь Паршин",
		note = "Разработчик · Android",
	),
	SearchHit.Person(
		title = "Дмитрий Орлов",
		note = "Разработчик · Backend",
	),
	SearchHit.Person(
		title = "Олег Кузнецов",
		note = "Менеджер · Продукт",
	),
	SearchHit.Chat(
		title = "Власов Кирилл Вадимович",
		note = "Личный чат",
	),
	SearchHit.Chat(
		title = "Команда дизайна",
		note = "Групповой чат",
	),
	SearchHit.Message(
		title = "JAS-7743 [BE][Android] Оплата заказа",
		time = "Сегодня",
		preview = "Власов: Да, залил в Figma, глянь последний фрейм",
	),
	SearchHit.Message(
		title = "Дизайн-ревью",
		time = "Вчера",
		preview = "Анна: @Власов посмотри макет, пожалуйста",
	),
	SearchHit.Message(
		title = "Команда дизайна",
		time = "5 окт",
		preview = "Игорь: Власов, созвон в 15:00",
	),
	SearchHit.File(
		title = "Payment_flow.pdf",
		note = "16 октября",
	),
)

internal fun searchHits(query: String, section: SearchSection): List<SearchHit> {
	val needle = query.trim()

	if (needle.isEmpty()) {
		return emptyList()
	}

	return catalog.filter { hit ->
		hit.fits(section) && hit.text().contains(needle, ignoreCase = true)
	}
}

private fun SearchHit.fits(section: SearchSection): Boolean = when (section) {
	SearchSection.ALL -> true
	SearchSection.CHATS -> this is SearchHit.Chat
	SearchSection.PEOPLE -> this is SearchHit.Person
	SearchSection.TEXT -> this is SearchHit.Message
	SearchSection.FILES -> this is SearchHit.File
}

private fun SearchHit.text(): String = when (this) {
	is SearchHit.Person -> "$title $note"
	is SearchHit.Chat -> "$title $note"
	is SearchHit.Message -> "$title $preview"
	is SearchHit.File -> "$title $note"
}
