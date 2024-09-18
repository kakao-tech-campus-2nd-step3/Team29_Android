package com.iguana.notai

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByName

internal fun Project.configureKotestAndroid() {
    configureKotest()
    configureJUnitAndroid()
}

internal fun Project.configureJUnitAndroid() {
    // android 블록에 접근
    val android = extensions.getByName<BaseExtension>("android")
    android.apply {
        testOptions {
            unitTests.all { it.useJUnitPlatform() }
        }
    }
}