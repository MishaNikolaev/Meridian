plugins {
	alias(libs.plugins.meridian.android.library)
	alias(libs.plugins.kotlin.serialization)
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(libs.bundles.coroutines)
	implementation(libs.dagger)
	kapt(libs.dagger.compiler)
	api(libs.ktorfit)
	api(libs.ktor.client.okhttp)
	implementation(libs.okhttp.logging.interceptor)
	api(libs.kotlinx.serialization.json)
	implementation(libs.ktor.client.content.negotiation)
	implementation(libs.ktor.serialization.kotlinx.json)
	implementation(libs.bouncycastle)
	api(projects.core.di)
}
