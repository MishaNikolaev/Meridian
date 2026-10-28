package com.nmichail.meridian.core.network.okhttp.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class LoggingInterceptor : Interceptor {

	private companion object {

		const val TAG = "OkHttp"
	}

	override fun intercept(chain: Interceptor.Chain): Response =
		HttpLoggingInterceptor { message ->
			Log.i(TAG, message)
		}.setLevel(HttpLoggingInterceptor.Level.BODY).intercept(chain)
}