plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    testImplementation(libs.junit4)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk)
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotlin.test)
}

gradlePlugin {
    plugins {
        register("androidHilt") {
            id = "iguana.android.hilt"
            implementationClass = "com.iguana.notai.HiltAndroidPlugin"
        }
        register("androidRoom") {
            id = "iguana.android.room"
            implementationClass = "com.iguana.notai.AndroidRoomPlugin"
        }
        register("kotlinHilt") {
            id = "iguana.kotlin.hilt"
            implementationClass = "com.iguana.notai.HiltKotlinPlugin"
        }
    }
}