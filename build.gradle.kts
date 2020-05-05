import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    kotlin("jvm") version "1.3.72"
    application
    `maven-publish`
    jacoco
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("com.jfrog.bintray") version "1.8.5"
}

val vitrailVersion = "0.0.1"

allprojects {
    ext {
        @Suppress("UNUSED_VARIABLE")
        val artifactVersion = vitrailVersion
    }
}

repositories {
    mavenCentral()
    jcenter()

    maven("https://plugins.gradle.org/m2/")
}

group = "com.wilgig"
version = vitrailVersion

val kotlinVersion = "1.3.72"
val spekVersion = "2.0.10"
val junitVersion = "5.6.2"
val mockitoVersion = "3.3.3"
val ktlintGradleVersion = "9.2.1"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.3")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    // spek requires kotlin-reflect, can be omitted if already in the classpath
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")

    testRuntimeOnly("org.jlleitschuh.gradle:ktlint-gradle:$ktlintGradleVersion")
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks

compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events = setOf(
            TestLogEvent.STARTED,
            TestLogEvent.PASSED,
            TestLogEvent.FAILED
        )
        showStandardStreams = true
    }
}

jacoco {
    toolVersion = "0.8.5"
}

tasks.jacocoTestReport {
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it) {
            exclude("**/MainKt.class")
        }
    }))

    reports {
        xml.isEnabled = true
        csv.isEnabled = false
        html.isEnabled = true
        html.destination = file("$buildDir/jacocoHtml")
    }
}

val testWithCoverage by tasks.registering {
    group = "verification"
    description = "Runs the unit tests with coverage."

    dependsOn(":test", ":jacocoTestReport", ":jacocoTestCoverageVerification")
    val jacocoTestReport = tasks.findByName("jacocoTestReport")
    jacocoTestReport?.mustRunAfter(tasks.findByName("test"))
    tasks.findByName("jacocoTestCoverageVerification")?.mustRunAfter(jacocoTestReport)
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

val publicationName = "vitrail"
configure<PublishingExtension> {
    repositories {
        maven("https://dl.bintray.com/jlandic/vitrail")
    }

    publications {
        publications.invoke {
            create<MavenPublication>(publicationName) {
                artifactId = publicationName
                groupId = "$group"
                version = vitrailVersion
                artifact(sourcesJar)
                from(components["java"])
            }
        }
    }
}

configure<com.jfrog.bintray.gradle.BintrayExtension> {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    publish = true
    setPublications(publicationName)
    pkg(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.PackageConfig> {
        repo = "vitrail"
        name = "vitrail"
        vcsUrl = "https://github.com/jlandic/vitrail"
        githubRepo = "jlandic/vitrail"
        desc = "Vitrail is a text generator using context-free grammars, inspired by projects like Tracery, or Grammy."
        setLicenses("MIT")

        version.apply {
            name = vitrailVersion
        }
    })
}

application {
    mainClassName = "com.wilgig.vitrail.MainKt"
}
