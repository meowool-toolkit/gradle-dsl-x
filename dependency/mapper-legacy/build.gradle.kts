plugins { kotlin; `kotlin-dsl`; kotlin("plugin.serialization") }

publication {
  data {
    artifactId = "toolkit-dependency-mapper-legacy"
    displayName = "Dependency Mapper for Gradle Toolkit"
    description = "Map all dependencies to classes and fields for easy calling in gradle scripts."
  }
  pluginClass = "${data.groupId}.toolkit.DependencyMapperPlugin"
}

dependencies.implementationOf(
  Libs.Ktor.Jsoup,
  Libs.Ktor.Client.OkHttp,
  Libs.Ktor.Client.Logging,
  Libs.Ktor.Client.Serialization,
  Libs.KotlinX.Serialization.Json,
  Libs.ByteBuddy.Byte.Buddy,
  Libs.Andreinc.Mockneat,
  Libs.Caffeine,
)