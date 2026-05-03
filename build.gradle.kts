plugins {
    java
    id("com.gradleup.shadow")
    id("com.modrinth.minotaur")
}

group = "io.github.lumine1909"
version = "2.5.1"

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
    implementation(project(":nms:nms_1_21_4"))
    implementation(project(":nms:nms_1_21_5"))
    implementation(project(":nms:nms_1_21_9"))
    implementation(project(":nms:nms_1_21_11"))
    implementation(project(":nms:nms_26_1"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    shadowJar {
        archiveVersion.set(version.toString())
        archiveFileName.set("CustomBiomeColors-${version}+1.20.5-26.1.2.jar")
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

modrinth {
    token.set(project.findProperty("modrinthKey") as? String ?: "")
    projectId.set("custombiomecolors_continue")
    versionNumber.set(version as String)
    versionName.set("CustomBiomeColors_Continue $version")
    versionType.set("release")
    uploadFile.set(tasks.shadowJar)
    loaders.addAll("bukkit", "paper", "purpur", "folia")

    gameVersions.addAll(generateVersions("1.20", 5, 6))
    gameVersions.addAll(generateVersions("1.21", 0, 11))
    gameVersions.addAll(generateVersions("26.1", 0, 2))
}

fun generateVersions(mm: String, start: Int, end: Int): List<String> = (start..end).map { if (it == 0) mm else "$mm.$it" }

subprojects {
    plugins.apply("java")

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        implementation("io.github.lumine1909:reflexion:0.3.2")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
}
