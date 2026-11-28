package com.nmichail.meridian.features.conversation.presentation.compose

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

@Composable
internal fun FollowComments(listState: LazyListState, newest: ChatLine?, target: Int) {
	LaunchedEffect(newest?.id) {
		if (newest == null || target < 0) {
			return@LaunchedEffect
		}
		val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
		val nearEnd = lastVisible == null || lastVisible >= target - 1
		if (newest.mine || nearEnd) {
			listState.animateScrollToItem(target)
		}
	}
}