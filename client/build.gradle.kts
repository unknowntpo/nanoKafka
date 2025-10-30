plugins {
    `java-library`
    signing
    id("com.vanniktech.maven.publish") version "0.30.0"
}

repositories {
    mavenCentral()
}

dependencies {
    // Depend on internal lib module
    implementation(project(":lib"))

    // Use JUnit Jupiter for testing
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Mockito for mocking
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

// Maven publishing configuration
afterEvaluate {
    signing {
        useGpgCmd()
        sign(publishing.publications)
    }
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()

    coordinates(
        groupId = project.group.toString(),
        artifactId = "nano-kafka-client",
        version = project.version.toString()
    )

    pom {
        name.set("nanoKafka Client")
        description.set("Client library for producing and consuming messages with nanoKafka.")
        url.set("https://github.com/unknowntpo/nanoKafka")

        licenses {
            license {
                name.set("Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }

        developers {
            developer {
                id.set("unknowntpo")
                name.set("Eric Chang")
                email.set("unknowntpo@apache.org")
            }
        }

        scm {
            connection.set("scm:git:https://github.com/unknowntpo/nanoKafka.git")
            developerConnection.set("scm:git:ssh://github.com/unknowntpo/nanoKafka.git")
            url.set("https://github.com/unknowntpo/nanoKafka")
        }
    }
}
