package com.iguana.notai

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

internal fun Project.configureKotest() {
    configureJUnit()
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    dependencies {
        "testImplementation"(libs.findLibrary("kotest.runner").get())
        "testImplementation"(libs.findLibrary("kotest.assertions").get())
    }
}

internal fun Project.configureJUnit() {
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}