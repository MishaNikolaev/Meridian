package com.nmichail.meridian.core.network.di

import com.nmichail.meridian.core.network.config.NetworkConfig
import com.nmichail.meridian.core.network.error.ApiErrorBody
import com.nmichail.meridian.core.network.error.GatewayCode
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.core.network.session.installSessionRefresh
import dagger.Module
import dagger.Provides
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Singleton
import kotlinx.serialization.json.Json

@Module
object NetworkModule {

	@Provides
	@Singleton
	@JvmStatic
	fun provideJson(): Json = Json {
		ignoreUnknownKeys = true
		explicitNulls = false
	}

	@Provides
	@Singleton
	@JvmStatic
	fun provideHttpClient(
		sessionRepository: SessionRepository,
		json: Json,
		refresher: SessionRefresher,
	): HttpClient = HttpClient(OkHttp) {
		expectSuccess = false
		install(ContentNegotiation) {
			json(json)
		}
		defaultRequest {
			contentType(ContentType.Application.Json)
			val token = sessionRepository.getAccessToken()
			if (!token.isNullOrBlank()) {
				header(HttpHeaders.Authorization, "Bearer $token")
			}
		}
		HttpResponseValidator {
			validateResponse { response ->
				if (!response.status.isSuccess()) {
					throw response.toGatewayException()
				}
			}
		}
	}.also { client ->
		client.installSessionRefresh(refresher)
	}

	@Provides
	@Singleton
	@JvmStatic
	fun provideKtorfit(
		client: HttpClient,
		config: NetworkConfig,
	): Ktorfit = Ktorfit.Builder()
		.httpClient(client)
		.baseUrl(config.baseUrl)
		.build()
}

private suspend fun HttpResponse.toGatewayException(): GatewayException {
	val error = runCatching { body<ApiErrorBody>() }.getOrNull()
	val raw = error?.code
	return GatewayException(
		code = raw?.let(GatewayCode::from) ?: GatewayCode.UNKNOWN,
		message = error?.message ?: status.description,
		httpStatus = raw?.toIntOrNull() ?: status.value,
	)
}
