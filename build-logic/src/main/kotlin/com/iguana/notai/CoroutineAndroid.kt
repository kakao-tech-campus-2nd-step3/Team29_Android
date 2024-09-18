package com.iguana.notai

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

internal fun Project.configureCoroutineAndroid() {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    configureCoroutineKotlin()
    dependencies {
        "implementation"(libs.findLibrary("coroutines.android").get())
    }
}

internal fun Project.configureCoroutineKotlin() {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    dependencies {
        "implementation"(libs.findLibrary("coroutines.core").get())
        "testImplementation"(libs.findLibrary("coroutines.test").get())
    }
}