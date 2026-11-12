import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("com.android.library")
	id("org.jetbrains.kotlin.android")
	id("org.jetbrains.kotlin.plugin.compose")
	id("meridian-detekt")
}

private val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

android {
	namespace = "com.nmichail.meridian" + project.path.replace(":", ".")
	compileSdk = libs.findVersion("project-compileSdk").get().requiredVersion.toInt()

	defaultConfig {
		minSdk = libs.findVersion("project-minSdk").get().requiredVersion.toInt()
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	compileOptions {
		val jvmVersion = libs.findVersion("jvmTarget").get().requiredVersion
		sourceCompatibility = JavaVersion.toVersion(jvmVersion)
		targetCompatibility = JavaVersion.toVersion(jvmVersion)
	}

	buildFeatures {
		compose = true
	}

	testOptions {
		unitTests.all {
			it.useJUnitPlatform()
		}
	}
}

kotlin {
	compilerOptions {
		val jvmVersion = libs.findVersion("jvmTarget").get().requiredVersion
		jvmTarget.set(JvmTarget.fromTarget(jvmVersion))
	}
}

tasks.named("preBuild") {
	dependsOn("detekt")
}

val kotlinVersion = libs.findVersion("kotlin").get().requiredVersion
configurations.configureEach {
	resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
}

dependencies {
	implementation(libs.findLibrary("kotlin-stdlib").get())
	implementation(platform(libs.findLibrary("androidx-compose-bom").get()))
	implementation(libs.findLibrary("compose-runtime").get())
	add("testRuntimeOnly", libs.findLibrary("test-junit-platform").get())
}
