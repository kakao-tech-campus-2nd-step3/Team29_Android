plugins {
    id("iguana.android.application")
    id("org.jetbrains.kotlin.android")
    id("iguana.android.room")
    id("iguana.android.hilt")
    id("iguana.kotlin.hilt")
}

android {
    compileSdk = 34

    namespace = "com.iguana.notai"

    defaultConfig {
        applicationId = "com.iguana.notai"
        versionCode = 1
        versionName = "1.0"
        minSdk = 26
        targetSdk = 34
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.feature.login)
    implementation(projects.feature.dashBoard)
    implementation(projects.feature.notetaking)
    implementation("com.kakao.sdk:v2-user:2.20.6")
}