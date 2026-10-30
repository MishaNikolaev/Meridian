plugins {
	alias(libs.plugins.meridian.android.library)
}

dependencies {
	implementation(libs.test.junit.api)
	implementation(libs.test.coroutines)
	implementation(libs.test.mockito.kotlin)
	implementation(libs.androidx.arch.core)
}
