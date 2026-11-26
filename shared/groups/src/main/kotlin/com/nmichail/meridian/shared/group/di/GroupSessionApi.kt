package com.nmichail.meridian.shared.group.di

import com.nmichail.meridian.shared.group.domain.repository.GroupRepository

interface GroupSessionApi {
	fun groupRepository(): GroupRepository
}
