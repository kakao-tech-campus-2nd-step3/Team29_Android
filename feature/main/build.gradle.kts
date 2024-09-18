plugins {
    id("iguana.android.feature")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.iguana.main"
}

dependencies {
    implementation(projects.feature.login)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.kotlinx.immutable)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
}