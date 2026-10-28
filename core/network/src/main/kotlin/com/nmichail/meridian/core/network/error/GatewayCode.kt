package com.nmichail.meridian.core.network.error

import com.nmichail.meridian.core.network.R

enum class GatewayCode(val message: Int) {

	UNKNOWN(R.string.error_unknown),
	USERNAME_TAKEN(R.string.error_username_taken),
	DEVICE_TAKEN(R.string.error_device_taken),
	USER_NOT_FOUND(R.string.error_user_not_found),
	DEVICE_NOT_FOUND(R.string.error_device_not_found),
	FORBIDDEN(R.string.error_forbidden),
	PHRASE_EXPIRED(R.string.error_phrase_expired),
	INVALID_SIGNATURE(R.string.error_invalid_signature),
	PHRASE_NOT_FOUND(R.string.error_phrase_not_found),
	UNAUTHORIZED(R.string.error_unauthorized),
	TOO_MANY_REQUESTS(R.string.error_too_many_requests),
	INVALID_USERNAME(R.string.error_invalid_username),
	INVALID_DISPLAY_NAME(R.string.error_invalid_display_name),
	INVALID_IDENTITY_KEY(R.string.error_invalid_identity_key),
	INVALID_SIGNED_PREKEY(R.string.error_invalid_signed_prekey),
	INVALID_PREKEYS(R.string.error_invalid_prekeys),
	INVALID_PREKEY(R.string.error_invalid_prekey),
	INVALID_ID(R.string.error_invalid_id),
	INVALID_PASSWORD(R.string.error_invalid_password),
	PASSWORD_NOT_SET(R.string.error_password_not_set),
	PASSWORD_MISMATCH(R.string.error_password_mismatch),
	SERVER_NOT_FOUND(R.string.error_server_not_found),
	INVALID_HOST(R.string.error_server_not_found),
	NOT_ON_SERVER(R.string.error_not_on_server),
	INVALID_DIRECT_CHAT(R.string.error_invalid_direct_chat),
	INVALID_TITLE(R.string.error_invalid_title),
	INVALID_MEMBERS(R.string.error_invalid_members),
	CHAT_NOT_FOUND(R.string.error_chat_not_found),
	MISSING_ENVELOPE(R.string.error_missing_envelope),
	;

	companion object {

		fun from(raw: String): GatewayCode =
			entries.firstOrNull { code -> code.name == raw } ?: UNKNOWN
	}
}