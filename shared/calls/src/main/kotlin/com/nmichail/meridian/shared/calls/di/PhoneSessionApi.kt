package com.nmichail.meridian.shared.calls.di

import com.nmichail.meridian.shared.calls.domain.repository.PhoneRepository

interface PhoneSessionApi {
	fun phoneRepository(): PhoneRepository
}
