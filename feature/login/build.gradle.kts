plugins {
    id("iguana.android.library")
    id("iguana.android.hilt")
    id("iguana.kotlin.hilt")
    id("iguana.android.feature")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.iguana.login"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.appcompat)
    implementation(libs.androidx.constraintlayout)
}