// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val compose_version by extra("1.0.0-beta09")
    val hilt_version by extra("2.37")
    val room_version by extra("2.3.0")
    val lifecycle_version by extra("2.3.1")
    val activity_version by extra("1.3.0-beta02")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${rootProject.extra["hilt_version"]}")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}