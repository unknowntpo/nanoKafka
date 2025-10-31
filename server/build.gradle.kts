plugins {
    application
    id("java")
    signing
    id("com.vanniktech.maven.publish") version "0.30.0"
    id("com.google.protobuf") version "0.9.4"
}

repositories {
    mavenCentral()
}

dependencies {
    // Depend on the lib module for core Kafka components
    implementation(project(":lib"))

    // gRPC dependencies
    implementation(libs.grpc.netty.shaded)
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    implementation(libs.protobuf.java)
    compileOnly(libs.javax.annotation.api)

    // Use JUnit Jupiter for testing
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Define the main class for the application
    mainClass.set("io.github.unknowntpo.server.KafkaBroker")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests
    useJUnitPlatform()
}

// Configure the JAR task to create an executable JAR
tasks.jar {
    dependsOn(":lib:jar")
    manifest {
        attributes(
            "Main-Class" to "io.github.unknowntpo.server.KafkaBroker"
        )
    }
    // Create a fat JAR with all dependencies
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

// Protobuf configuration
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.get()}"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                create("grpc")
            }
        }
    }
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
        artifactId = "nano-kafka-server",
        version = project.version.toString()
    )

    pom {
        name.set("nanoKafka Server")
        description.set("Broker server for nanoKafka.")
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
