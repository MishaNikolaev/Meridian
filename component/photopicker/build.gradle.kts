plugins {
	alias(libs.plugins.meridian.android.library)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.ksp)
	alias(libs.plugins.ktorfit)
	id("org.jetbrains.kotlin.kapt")
}

dependencies {
	implementation(libs.bundles.coroutines)
	implementation(libs.dagger)
	kapt(libs.dagger.compiler)
	implementation(libs.ktorfit)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.bundles.compose)
	implementation(libs.androidx.activity.compose)
	api(libs.coil.compose)
	implementation(libs.coil.network.ktor3)
	implementation(libs.androidx.exifinterface)
	implementation(libs.krop.ui)
	implementation(projects.component.ui)
	implementation(projects.core.network)
	testImplementation(libs.bundles.test.junit5)
	testImplementation(libs.test.coroutines)
	testImplementation(libs.ktor.client.mock)
	testImplementation(libs.ktor.client.content.negotiation)
	testImplementation(libs.ktor.serialization.kotlinx.json)
}

afterEvaluate {
	tasks.matching { it.name.startsWith("kaptGenerateStubs") }.configureEach {
		val variant = name.removePrefix("kaptGenerateStubs").removeSuffix("Kotlin")
		val kspTaskName = "ksp${variant}Kotlin"
		if (tasks.names.contains(kspTaskName)) {
			dependsOn(kspTaskName)
		}
	}
}