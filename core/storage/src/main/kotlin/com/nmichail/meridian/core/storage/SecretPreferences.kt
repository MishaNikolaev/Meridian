package com.nmichail.meridian.core.storage

import android.content.SharedPreferences
import androidx.core.content.edit

fun SharedPreferences.openSecret(cipher: SecretCipher, key: String): String? {
	val stored = getString(key, null) ?: return null
	if (!cipher.isSealed(stored)) {
		edit { putString(key, cipher.seal(stored)) }
		return stored
	}
	val opened = cipher.open(stored) ?: return null
	if (!cipher.isCurrent(stored)) {
		edit { putString(key, cipher.seal(opened)) }
	}
	return opened
}

fun SharedPreferences.saveSecret(cipher: SecretCipher, key: String, value: String) {
	edit { putString(key, cipher.seal(value)) }
}
