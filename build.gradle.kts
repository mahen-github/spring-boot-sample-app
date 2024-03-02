plugins {
    java
    application
    jacoco
    checkstyle
    id("maven-publish")
    id("com.diffplug.spotless")
    id("com.jfrog.artifactory")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("ch.ergon.gradle.goodies.versioning")
}

group = "dev.sample"

versioning {
    match = "release-*"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
application {
    mainClass.set("dev.sample.App")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}

tasks.withType<JavaCompile> {
    val compilerArgs = options.compilerArgs
    compilerArgs.add("-Xdoclint:all,-missing")
    compilerArgs.add("-Xlint:all")
    options.encoding = "UTF-8"
}

publishing {
    publications {
        create<MavenPublication>("Maven") {
            groupId = project.group.toString()
            artifactId = project.name
            from(components["java"])
        }
    }
}

spotless {
    format("misc") {
        target("*.md", ".gitignore", "*.yml", "*.properties")
        indentWithSpaces()
        trimTrailingWhitespace()
        endWithNewline()
    }
    java {
        target(
            project.fileTree(project.rootDir) {
                include("src/*/java/**/*.java")
            },
        )
        googleJavaFormat("1.17.0").reflowLongStrings()
        removeUnusedImports()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("0.50.0")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

testing {
    suites {
        named("test", JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation("org.springframework.boot:spring-boot-starter-web")
            }
        }
    }
}

jacoco {
    tasks.jacocoTestReport {
        dependsOn(tasks.test)
    }
    tasks.jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = "0.8".toBigDecimal()
                }
            }
        }
    }
}

tasks.test {
    testLogging {
        setExceptionFormat("full")
        showStackTraces = true
    }
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    finalizedBy(tasks.jacocoTestCoverageVerification)
}

checkstyle {
    isShowViolations = true
    maxWarnings = 0
    maxErrors = 0
    isIgnoreFailures = false
    configProperties = mapOf("suppressionFile" to project.file("config/checkstyle/suppressions.xml"))
    tasks.withType<Checkstyle> {
        reports.xml.required = false
        reports.html.required = true
    }
}

tasks.compileJava {
    dependsOn(
        "spotlessJavaApply",
        "spotlessKotlinGradleApply",
        "spotlessMiscApply",
    )
}
