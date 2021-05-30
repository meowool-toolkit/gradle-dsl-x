@file:Suppress("SpellCheckingInspection")

pluginManagement {
  repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
    google()
    gradlePluginPortal()
  }
}

plugins {
  id("com.meowool.toolkit.gradle-dsl-x") version "1.8"
}

buildscript {
  configurations.all {
    // Check for updates every build
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
  }
}

gradleDslX {
  useMeowoolSpec()
  configureAllKotlinCompile {
    addFreeCompilerArgs("-Xopt-in=annotation.InternalGradleDslXApi")
  }
}

importProjects(
  includeDir = rootDir,
  excludeDirs = arrayOf(file("core/src/test"))
)