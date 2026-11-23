package com.nmichail.meridian.features.groups.info.presentation.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.photo.PhotoFace
import com.nmichail.meridian.component.ui.R as UiR
import com.nmichail.meridian.component.ui.field.MUiTextField
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette
import com.nmichail.meridian.features.groups.info.R
import com.nmichail.meridian.shared.organization.domain.entity.OrganizationMember
import com.nmichail.meridian.features.groups.info.presentation.GroupInfoIntent

@Composable
internal fun AddMembers(
	candidates: List<OrganizationMember>,
	query: String,
	onIntent: (GroupInfoIntent) -> Unit,
) {

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(palette.page),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 14.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp),
		) {
			Image(
				painter = painterResource(UiR.drawable.ic_back),
				contentDescription = null,
				colorFilter = ColorFilter.tint(palette.textPrimary),
				modifier = Modifier
					.size(16.dp)
					.clickable { onIntent(GroupInfoIntent.CancelAdd) },
			)
			Text(
				text = stringResource(R.string.group_add_title),
				style = MeridianType.titleCard,
				color = palette.textPrimary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		}

		MUiTextField(
			model = MUiTextFieldModel(
				icon = MUiFieldIcon.Search,
				value = query,
				placeholder = stringResource(R.string.group_add_search),
			),
			onValueChange = { onIntent(GroupInfoIntent.ChangeQuery(query = it)) },
			container = palette.field,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
		)

		if (candidates.isEmpty()) {
			Text(
				text = stringResource(R.string.group_add_empty),
				style = MeridianType.bodyDefault,
				color = palette.textSecondary,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
			)
		} else {
			LazyColumn(modifier = Modifier.fillMaxWidth()) {
				items(items = candidates, key = { person -> person.userId }) { person ->
					MemberLine(person = person, onIntent = onIntent)
				}
			}
		}
	}
}

@Composable
private fun MemberLine(
	person: OrganizationMember,
	onIntent: (GroupInfoIntent) -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onIntent(GroupInfoIntent.Pick(userId = person.userId)) }
			.padding(horizontal = 16.dp, vertical = 8.dp),
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		PhotoFace(
			name = person.displayName,
			userId = person.userId,
			size = 46.dp,
			style = MeridianType.captionDefault,
		)
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = person.displayName,
				style = MeridianType.bodyName.copy(fontWeight = FontWeight.SemiBold),
				color = palette.textPrimary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
			Text(
				text = person.username,
				style = MeridianType.bodySmall,
				color = palette.textSecondary,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis,
			)
		}
	}
}
