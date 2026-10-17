package com.nmichail.meridian.shared.session.data.api

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.shared.session.data.model.AuthorizationRequest
import com.nmichail.meridian.shared.session.data.model.LogoutRequest
import com.nmichail.meridian.shared.session.data.model.PasswordAuthorizationRequest
import com.nmichail.meridian.shared.session.data.model.RefreshRequest
import com.nmichail.meridian.shared.session.data.model.WorkspaceAuthorizationRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface AuthorizationApi {

	@POST("api/gateway/authorization")
	suspend fun authorize(@Body request: AuthorizationRequest): AuthSession

	@POST("api/gateway/authorization/password")
	suspend fun authorizeByPassword(@Body request: PasswordAuthorizationRequest): AuthSession

	@POST("api/gateway/authorization/workspace")
	suspend fun authorizeWorkspace(@Body request: WorkspaceAuthorizationRequest): AuthSession

	@POST("api/gateway/authorization/refresh")
	suspend fun refresh(@Body request: RefreshRequest): AuthSession

	@POST("api/gateway/logout")
	suspend fun logout(@Body request: LogoutRequest)
}
