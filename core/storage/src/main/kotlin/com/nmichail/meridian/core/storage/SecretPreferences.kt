package com.nmichail.meridian.core.storage

import android.content.SharedPreferences
import androidx.core.content.edit

fun SharedPreferences.openSecret(cipher: SecretCipher, key: String): String? {
	val stored = getString(key, null) ?: return null
	if (cipher.isSealed(stored)) {
		return cipher.open(stored)
	}
	edit { putString(key, cipher.seal(stored)) }
	return stored
}

fun SharedPreferences.Editor.putSecret(cipher: SecretCipher, key: String, value: String) {
	putString(key, cipher.seal(value))
}
