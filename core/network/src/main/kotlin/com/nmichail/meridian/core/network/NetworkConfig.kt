package com.nmichail.meridian.core.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConfig @Inject constructor() {

	val baseUrl: String = "https://example.com/"
}
