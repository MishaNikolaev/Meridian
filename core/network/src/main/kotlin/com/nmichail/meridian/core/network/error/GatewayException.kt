package com.nmichail.meridian.core.network.error

import com.nmichail.meridian.core.network.R
import java.io.IOException

class GatewayException(
	val code: String,
	message: String,
) : Exception(message)

fun Throwable.userMessage(): Int =
	when (this) {
		is java.util.concurrent.CancellationException -> throw this
		is GatewayException -> displayMessage()
		is IOException -> R.string.error_no_connection
		else -> R.string.error_unknown
	}

fun GatewayException.displayMessage(): Int = messages[code] ?: R.string.error_unknown

private val messages = mapOf(
	"USERNAME_TAKEN" to R.string.error_username_taken,
	"DEVICE_TAKEN" to R.string.error_device_taken,
	"USER_NOT_FOUND" to R.string.error_user_not_found,
	"DEVICE_NOT_FOUND" to R.string.error_device_not_found,
	"FORBIDDEN" to R.string.error_forbidden,
	"PHRASE_EXPIRED" to R.string.error_phrase_expired,
	"INVALID_SIGNATURE" to R.string.error_invalid_signature,
	"PHRASE_NOT_FOUND" to R.string.error_phrase_not_found,
	"UNAUTHORIZED" to R.string.error_unauthorized,
	"TOO_MANY_REQUESTS" to R.string.error_too_many_requests,
	"INVALID_USERNAME" to R.string.error_invalid_username,
	"INVALID_DISPLAY_NAME" to R.string.error_invalid_display_name,
	"INVALID_IDENTITY_KEY" to R.string.error_invalid_identity_key,
	"INVALID_SIGNED_PREKEY" to R.string.error_invalid_signed_prekey,
	"INVALID_PREKEYS" to R.string.error_invalid_prekeys,
	"INVALID_PREKEY" to R.string.error_invalid_prekey,
	"INVALID_ID" to R.string.error_invalid_id,
	"INVALID_PASSWORD" to R.string.error_invalid_password,
	"PASSWORD_NOT_SET" to R.string.error_password_not_set,
	"PASSWORD_MISMATCH" to R.string.error_password_mismatch,
	"SERVER_NOT_FOUND" to R.string.error_server_not_found,
	"INVALID_HOST" to R.string.error_server_not_found,
	"NOT_ON_SERVER" to R.string.error_not_on_server,
	"INVALID_DIRECT_CHAT" to R.string.error_invalid_direct_chat,
	"INVALID_TITLE" to R.string.error_invalid_title,
	"INVALID_MEMBERS" to R.string.error_invalid_members,
	"CHAT_NOT_FOUND" to R.string.error_chat_not_found,
	"MISSING_ENVELOPE" to R.string.error_missing_envelope,
)
