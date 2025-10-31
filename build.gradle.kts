// Root project - apply publishing plugin with `apply false` to fix shared build service error
// When the same plugin is applied to multiple sibling projects (client and server),
// Gradle cannot share the SonatypeRepositoryBuildService between different classloaders.
// Solution: Apply the plugin in root with `apply false`, then apply it in subprojects.
// See: https://github.com/gradle/gradle/issues/17559
plugins {
    id("com.vanniktech.maven.publish") version "0.30.0" apply false
}
