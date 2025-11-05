plugins {
    kotlin("jvm")
    kotlin("kapt")
    application
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":annotations"))
    kapt(project(":processor"))
}
repositories {
    mavenCentral()
}

application {
    mainClass.set("com.example.app.MainKt")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/source/kapt/main")
    }
}
