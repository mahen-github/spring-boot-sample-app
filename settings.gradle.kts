pluginManagement {
    plugins {
        id("java")
        id("application")
        id("com.diffplug.spotless") version "6.25.0"
        id("com.jfrog.artifactory") version "4.26.3"
        id("ch.ergon.gradle.goodies.versioning") version "1.0.0"
        id("org.springframework.boot") version "3.0.4"
        id("io.spring.dependency-management") version "1.1.0"
    }
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.springframework.org/release")
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.springframework.org/release")
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

rootProject.name = "spring-boot-sample-app"
