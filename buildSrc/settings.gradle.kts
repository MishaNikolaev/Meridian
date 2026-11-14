pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
	}

	versionCatalogs {
		create("libs") {
			from(files(File(settingsDir.parentFile, "buildsystem/libs.versions.toml")))
		}
	}
}

rootProject.name = "buildSrc"
