/*
 * Copyright (c) 2021. The Meowool Organization Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 * In addition, if you modified the project, you must include the Meowool
 * organization URL in your code file: https://github.com/meowool
 *
 * 如果您修改了此项目，则必须确保源文件中包含 Meowool 组织 URL: https://github.com/meowool
 */
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.meowool.gradle.toolkit.publisher

import DirectoryDestination
import DokkaFormat
import MavenLocalDestination
import PublishingDestination
import SonatypeDestination
import com.meowool.gradle.toolkit.publisher.internal.parentPublication
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.plugin.devel.PluginDeclaration
import java.io.File
import java.nio.file.Path

/**
 * The extension for publishing publication.
 *
 * @author 凛 (https://github.com/RinOrz)
 */
class PublicationExtension(internal val project: Project) {

  /**
   * If the value is `true`, when the project has applied the [PublisherPlugin] but is incompatible, a warning message
   * will be printed. If the value is `false`, no warning message is printed and the project is skipped.
   *
   * Note that when this value has not been modified, this value defaults to the value of the root project.
   */
  var showIncompatibleWarnings: Boolean? = null
    get() = field ?: project.parentPublication?.showIncompatibleWarnings ?: true

  /**
   * The data of this publication.
   */
  val data: PublicationData = PublicationData(project)

  /**
   * The plugin class for this Gradle plugin publication.
   *
   * If this value is `null`, it means this is a Maven publication, otherwise this publication will be regarded
   * as a Gradle plugin.
   *
   * @see PublicationData
   * @see PluginDeclaration.implementationClass
   */
  var pluginClass: String? = null

  /**
   * The destinations of this publication to publishing to.
   *
   * If the list of destinations is empty, [SonatypeDestination] will be used by default.
   *
   * Note that this value will inherit the value set in the root project.
   */
  val destinations: MutableSet<PublishingDestination> = mutableSetOf<PublishingDestination>().also {
    project.parentPublication?.destinations?.apply(it::addAll)
  }

  /**
   * The output format of documents generated by dokka.
   * This is related to the javadoc of the publication, [for more details](https://github.com/Kotlin/dokka).
   *
   * Note that this value will initially use the value set in the root project.
   */
  var dokkaFormat: DokkaFormat? = null
    get() = field ?: project.parentPublication?.dokkaFormat ?: DokkaFormat.Html

  /**
   * Whether to sign the release version of this publication.
   * For more details see [Gradle documentation](https://docs.gradle.org/current/userguide/signing_plugin.html).
   *
   * Note that this value will initially use the value set in the root project.
   */
  var isSignRelease: Boolean? = null
    get() = field ?: project.parentPublication?.isSignRelease ?: true

  /**
   * Whether to sign the snapshot version of this publication.
   * For more details see [Gradle documentation](https://docs.gradle.org/current/userguide/signing_plugin.html).
   *
   * Note that this value will initially use the value set in the root project.
   */
  var isSignSnapshot: Boolean? = null
    get() = field ?: project.parentPublication?.isSignSnapshot ?: false

  /**
   * Returns `true` if this publication is a snapshot version.
   *
   * @see [PublicationData.version]
   */
  val isSnapshotVersion: Boolean get() = data.versionOrDefault().endsWith("-SNAPSHOT")

  /**
   * Returns `true` if this publication is a local version.
   *
   * The local version is specially defined. When this value is true, the publication will only be published
   * to [RepositoryHandler.mavenLocal], ignoring all [destinations].
   *
   * @see [PublicationData.version]
   */
  val isLocalVersion: Boolean get() = data.versionOrDefault().contains("LOCAL")

  /**
   * Configures the data of this publication with [configuration].
   */
  fun data(configuration: PublicationData.() -> Unit) = data.apply(configuration)

  /**
   * Signs the release version of this publication.
   *
   * For more details see [Gradle documentation](https://docs.gradle.org/current/userguide/signing_plugin.html).
   *
   * @param isSign Whether to sign release version.
   */
  fun signRelease(isSign: Boolean = true) {
    isSignRelease = isSign
  }

  /**
   * Signs the snapshot version of this publication.
   *
   * For more details see [Gradle documentation](https://docs.gradle.org/current/userguide/signing_plugin.html).
   *
   * @param isSign Whether to sign snapshot version.
   */
  fun signSnapshot(isSign: Boolean = true) {
    isSignSnapshot = isSign
  }

  /**
   * Sets the output [format] of documents generated by dokka.
   *
   * This is related to the javadoc of the publication, [for more details](https://github.com/Kotlin/dokka).
   */
  fun dokkaFormat(format: DokkaFormat) {
    dokkaFormat = format
  }

  /**
   * Adds a publishing [destinations] for this publication.
   */
  fun publishTo(vararg destinations: PublishingDestination) {
    this.destinations += destinations
  }

  /**
   * Adds a destination to publish this publication to the Maven Local repository.
   *
   * @see MavenLocalDestination
   */
  fun publishToMavenLocal() = publishTo(MavenLocalDestination)

  /**
   * Adds a destination to publish this publication to the repository of specified directory.
   *
   * @param releasesPath The directory path to releases repository.
   * @param snapshotsPath The directory path to snapshots repository.
   *
   * @see DirectoryDestination
   */
  fun publishToDirectory(releasesPath: String, snapshotsPath: String = releasesPath) = publishTo(
    DirectoryDestination(releasesPath, snapshotsPath)
  )

  /**
   * Adds a destination to publish this publication to the repository of specified directory.
   *
   * @param releases The directory path to releases repository.
   * @param snapshots The directory path to snapshots repository.
   *
   * @see DirectoryDestination
   */
  fun publishToDirectory(releases: Path, snapshots: Path = releases) = publishTo(
    DirectoryDestination(releases, snapshots)
  )

  /**
   * Adds a destination to publish this publication to the repository of specified directory.
   *
   * @param releases The file (directory) to releases repository.
   * @param snapshots The file (directory) to snapshots repository.
   *
   * @see DirectoryDestination
   */
  fun publishToDirectory(releases: File, snapshots: File = releases) = publishTo(
    DirectoryDestination(releases, snapshots)
  )

  /**
   * Adds a destination to publish this publication to the Sonatype OSS repository.
   *
   * @param s01 Publish to new url of Sonatype OSS, [see](https://central.sonatype.org/news/20210223_new-users-on-s01/)
   *
   * @see SonatypeDestination
   */
  fun publishToSonatype(s01: Boolean = true) = publishTo(SonatypeDestination(s01))
}
