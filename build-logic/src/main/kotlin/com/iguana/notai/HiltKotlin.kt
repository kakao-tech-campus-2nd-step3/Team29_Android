package com.iguana.notai

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureHiltKotlin() {
    with(pluginManager) {
        apply("org.jetbrains.kotlin.kapt")
    }
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    dependencies {
        "implementation"(libs.findLibrary("hilt.core").get())
        "kapt"(libs.findLibrary("hilt.compiler").get())
    }
}

internal class HiltKotlinPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            configureHiltKotlin()
        }
    }
}