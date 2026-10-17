package com.nmichail.meridian.core.network.session

import com.nmichail.meridian.core.network.error.GatewayException
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.Sender
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders

private const val UNAUTHORIZED = "UNAUTHORIZED"

fun HttpClient.installSessionRefresh(refresher: SessionRefresher) {
	plugin(HttpSend).intercept { request ->
		try {
			execute(request)
		} catch (error: GatewayException) {
			if (error.code != UNAUTHORIZED || !request.canRefresh()) {
				throw error
			}
			retry(request, refresher, error)
		}
	}
}

private suspend fun Sender.retry(
	request: HttpRequestBuilder,
	refresher: SessionRefresher,
	error: GatewayException,
): HttpClientCall {
	val session = refresher.refresh() ?: run {
		refresher.expire()
		throw error
	}
	request.headers.remove(HttpHeaders.Authorization)
	request.headers.append(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
	return try {
		execute(request)
	} catch (retry: GatewayException) {
		if (retry.code == UNAUTHORIZED) {
			refresher.expire()
		}
		throw retry
	}
}

private fun HttpRequestBuilder.canRefresh(): Boolean {
	val path = url.build().encodedPath
	val bearer = headers[HttpHeaders.Authorization] != null
	return bearer && !path.contains("/authorization") && !path.contains("/registration") && !path.contains("/logout")
}
