plugins {
    id("iguana.android.library")
}

android {
    namespace = "com.iguana.domain"
}
dependencies {
    implementation(projects.core.data)
}