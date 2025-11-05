plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("kapt") version "2.2.20"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    // Annotation processing
    kapt(project(":processor"))
    implementation(project(":annotations"))

    // Code generation
    implementation("com.squareup:kotlinpoet:1.16.0")

    // Testing
    testImplementation(kotlin("test"))
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/source/kapt/main")
    }
}

tasks.test {
    useJUnitPlatform()
}
