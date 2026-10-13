import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
	id("io.gitlab.arturbosch.detekt")
}

private val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
private val jvmTargetVersion = libs.findVersion("jvmTarget").get().requiredVersion

detekt {
	toolVersion = libs.findVersion("detekt").get().toString()

	source.setFrom("src")
	config.setFrom("$rootDir/config/detekt.yml")

	parallel = true
	ignoreFailures = false
}

dependencies {
	"detektPlugins"(libs.findLibrary("detekt-formatting").get())
	"detektPlugins"(libs.findLibrary("detekt-rules-libraries").get())
}

tasks.withType<Detekt>().configureEach {
	jvmTarget = jvmTargetVersion
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
	jvmTarget = jvmTargetVersion
}
