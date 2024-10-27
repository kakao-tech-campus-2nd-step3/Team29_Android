import java.util.Properties

plugins {
    id("iguana.android.library")
    id("iguana.android.hilt")
    id("iguana.kotlin.hilt")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.iguana.data"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val apiBaseUrl: String = getApiBaseUrl()
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
    }
}
dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.retrofit.gson)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    implementation(libs.room.runtime)
    implementation(libs.coroutines.android)
    implementation(projects.core.domain) // domain 에 있는 걸 구현하므로
    implementation(libs.okhttp.logging)
}

// local.properties 파일에서 API_BASE_URL 읽어오는 함수
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