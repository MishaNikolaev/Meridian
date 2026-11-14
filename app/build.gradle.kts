import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("org.jetbrains.kotlin.plugin.compose")
	id("org.jetbrains.kotlin.kapt")
	id("meridian-detekt")
}

android {
	namespace = Config.NAMESPACE
	compileSdk = Config.COMPILE_SDK

	defaultConfig {
		applicationId = Config.APPLICATION_ID
		minSdk = Config.MIN_SDK
		targetSdk = Config.TARGET_SDK
		versionCode = Config.CODE
		versionName = Config.APP_VERSION
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	compileOptions {
		val jvmVersion = libs.versions.jvmTarget.get()
		sourceCompatibility = JavaVersion.toVersion(jvmVersion)
		targetCompatibility = JavaVersion.toVersion(jvmVersion)
	}

	buildFeatures {
		compose = true
	}
}

kotlin {
	compilerOptions {
		jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
	}
}

val kotlinVersion = libs.versions.kotlin.get()
configurations.configureEach {
	resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
}

kapt {
	correctErrorTypes = true
}

tasks.named("preBuild") {
	dependsOn("detekt")
}

dependencies {
	implementation(libs.kotlin.stdlib)
	implementation(libs.bundles.coroutines)
	implementation(libs.cicerone)
	implementation(libs.dagger)
	kapt(libs.dagger.compiler)

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.bundles.compose)

	implementation(projects.component.ui)
	implementation(projects.component.photopicker)
	implementation(projects.core.navigation)
	implementation(projects.core.network)
	implementation(projects.core.di)
	implementation(projects.core.validation)
	implementation(projects.shared.session)
	implementation(projects.shared.chat)
	implementation(projects.features.mainhost)
	implementation(projects.features.chat)
	implementation(projects.features.groups)
	implementation(projects.features.search)
	implementation(projects.features.conversation)
	implementation(projects.features.contacts)
	implementation(projects.features.calls)
	implementation(projects.features.profile)
	implementation(projects.features.person)
	implementation(projects.features.registration)
	implementation(projects.features.authorization.workspace)
	implementation(projects.features.authorization.signin)
	implementation(projects.features.authorization.splash)

	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}
