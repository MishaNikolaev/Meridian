package com.nmichail.meridian.core.storage

import android.content.SharedPreferences
import androidx.core.content.edit

fun SharedPreferences.openSecret(cipher: SecretCipher, key: String): String? {
	val saved = getString(key, null) ?: return null
	if (cipher.isSealed(saved)) {
		return cipher.open(saved)
	}
	edit { putString(key, cipher.seal(saved)) }
	return saved
}

fun SharedPreferences.Editor.putSecret(
	cipher: SecretCipher,
	key: String,
	value: String,
) {
	putString(key, cipher.seal(value))
}
