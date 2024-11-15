import guru.nidi.graphviz.attribute.Font.config
import java.util.Properties

plugins {
    id("iguana.android.application")
    id("org.jetbrains.kotlin.android")
    id("iguana.android.room")
    id("iguana.android.hilt")
    id("iguana.kotlin.hilt")
}
fun getLocalProperty(key: String, defaultValue: String = ""): String {
    val localProperties = project.rootProject.file("local.properties")
    if (localProperties.exists()) {
        val properties = Properties().apply { load(localProperties.inputStream()) }
        return properties.getProperty(key, defaultValue)
    }
    return defaultValue
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("/Users/aengzu/AndroidStudioProjects/NOTAI_android/app/key.jks")
            storePassword = getLocalProperty("RELEASE_STORE_PASSWORD")
            keyAlias = getLocalProperty("RELEASE_KEY_ALIAS")
            keyPassword = getLocalProperty("RELEASE_KEY_PASSWORD")
        }
    }
    compileSdk = 34

    namespace = "com.iguana.notai"

    defaultConfig {
        applicationId = "com.iguana.notai"
        versionCode = 1
        versionName = "1.0"
        minSdk = 26
        targetSdk = 34
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val apiBaseUrl: String = getApiBaseUrl()
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        signingConfig = signingConfigs.getByName("release")
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
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
    implementation("com.kakao.sdk:v2-user:2.20.6")
}

fun getApiBaseUrl(): String {
    val localProperties = project.rootProject.file("local.properties")
    if (localProperties.exists()) {
        val properties = Properties().apply {
            load(localProperties.inputStream())
        }
        return properties.getProperty("API_BASE_URL", "https://example.com/")
    }
    return "https://example.com/" // 기본값
}