package com.nmichail.meridian.component.photopicker.data

import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

internal fun photoUpload(bytes: ByteArray, contentType: String): MultiPartFormDataContent = MultiPartFormDataContent(
	formData {
		append(
			key = "file",
			value = bytes,
			headers = Headers.build {
				append(HttpHeaders.ContentType, contentType)
				append(HttpHeaders.ContentDisposition, "filename=\"photo\"")
			},
		)
	},
)
