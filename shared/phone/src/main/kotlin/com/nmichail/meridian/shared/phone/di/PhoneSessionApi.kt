package com.nmichail.meridian.shared.phone.di

import com.nmichail.meridian.shared.phone.domain.repository.PhoneRepository

interface PhoneSessionApi {
	fun phoneRepository(): PhoneRepository
}
