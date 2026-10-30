package com.nmichail.meridian.core.network.session

import com.nmichail.meridian.core.network.error.GatewayCode
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.error.SessionRefreshException
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.Sender
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders

fun HttpClient.installSessionRefresh(refresher: SessionRefresher) {
	plugin(HttpSend).intercept { request ->
		request.useFreshAccess(refresher)
		try {
			execute(request)
		} catch (error: GatewayException) {
			if (error.code != GatewayCode.UNAUTHORIZED || !request.canRefresh()) {
				throw error
			}
			retry(request, refresher)
		}
	}
}

private suspend fun HttpRequestBuilder.useFreshAccess(refresher: SessionRefresher) {
	val token = headers[HttpHeaders.Authorization]?.removePrefix("Bearer ") ?: return
	if (!accessTokenExpired(token)) {
		return
	}
	val session = refresher.refresh() ?: refresher.refreshFailed()
	headers.remove(HttpHeaders.Authorization)
	headers.append(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
}

private suspend fun Sender.retry(
	request: HttpRequestBuilder,
	refresher: SessionRefresher,
): HttpClientCall {
	val session = refresher.refresh() ?: refresher.refreshFailed()
	request.headers.remove(HttpHeaders.Authorization)
	request.headers.append(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
	return try {
		execute(request)
	} catch (retry: GatewayException) {
		if (retry.code == GatewayCode.UNAUTHORIZED) {
			refresher.refreshFailed()
		}
		throw retry
	}
}

private fun SessionRefresher.refreshFailed(): Nothing {
	expire(notify = false)
	throw SessionRefreshException()
}

private fun HttpRequestBuilder.canRefresh(): Boolean {
	val path = url.build().encodedPath
	val bearer = headers[HttpHeaders.Authorization] != null
	return bearer && !path.contains("/authorization") && !path.contains("/registration") && !path.contains("/logout")
}

fun accessTokenExpired(token: String): Boolean {
	val payload = token.split('.').getOrNull(PAYLOAD) ?: return false
	val decoded = decode(payload) ?: return false
	val exp = EXP.find(decoded)?.groupValues?.get(1)?.toLongOrNull() ?: return false
	return exp <= System.currentTimeMillis() / MILLIS
}

private fun decode(payload: String): String? {
	val remainder = payload.length % PAD
	val padded = if (remainder == 0) payload else payload + "=".repeat(PAD - remainder)
	return try {
		String(java.util.Base64.getUrlDecoder().decode(padded), Charsets.UTF_8)
	} catch (_: IllegalArgumentException) {
		null
	}
}

private const val PAYLOAD = 1
private const val PAD = 4
private const val MILLIS = 1000L

private val EXP = Regex("\"exp\":(\\d+)")
