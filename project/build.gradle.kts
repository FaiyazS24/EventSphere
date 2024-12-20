plugins {
    alias(libs.plugins.android.application) apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.0")
        classpath("com.google.gms:google-services:4.3.15") // Latest stable version
    }
}
