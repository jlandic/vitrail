plugins {
    id("com.eden.orchidPlugin") version "0.20.0"
}

val artifactVersion = project.property("artifactVersion") as String

repositories {
    jcenter()
    maven(url = "https://kotlin.bintray.com/kotlinx/")
}

dependencies {
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidDocs:0.20.0")
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidKotlindoc:0.20.0")
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidPluginDocs:0.20.0")
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidGithub:0.20.0")
}

orchid {
    theme = "Editorial"
    baseUrl = "https://jlandic.github.io/vitrail"
    version = artifactVersion
    args = listOf("--experimentalSourceDoc")
    githubToken = System.getenv("GH_TOKEN") ?: ""
}
