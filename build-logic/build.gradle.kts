plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidHilt") {
            id = "iguana.android.hilt"
            implementationClass = "com.iguana.notai.HiltAndroidPlugin"
        }
        register("kotlinHilt") {
            id = "iguana.kotlin.hilt"
            implementationClass = "com.iguana.notai.HiltKotlinPlugin"
        }
        register("androidRoom") {
            id = "iguana.android.room"
            implementationClass = "com.iguana.notai.AndroidRoomPlugin"
        }
    }
}