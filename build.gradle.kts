plugins {
    java
    id("com.gradleup.shadow")
}

group = "io.github.lumine1909"
version = "2.3.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":plugin"))
    implementation(project(":core"))

    implementation(project(":nms:nms_1_20_5"))
    implementation(project(":nms:nms_1_21"))
    implementation(project(":nms:nms_1_21_3"))
    implementation(project(":nms:nms_1_21_5"))
    implementation(project(":nms:nms_1_21_9"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveVersion.set(version.toString())
        archiveFileName.set("CustomBiomeColors-${version}-MC-1.20.5-1.21.10.jar")
        archiveClassifier.set("")
        mergeServiceFiles()

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        subprojects.forEach { sub ->
            dependsOn(sub.tasks.jar)
            from(sub.tasks.jar.flatMap { it.archiveFile }.map { zipTree(it) })
        }
    }
    assemble {
        dependsOn(shadowJar)
    }
    project(":plugin") {
        tasks.withType<ProcessResources> {
            filteringCharset = Charsets.UTF_8.name()
            val props = mapOf("version" to rootProject.version)
            inputs.properties(props)
            filesMatching("plugin.yml") {
                expand(props)
            }
        }
    }
}

subprojects {
    plugins.apply("java")

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
}
