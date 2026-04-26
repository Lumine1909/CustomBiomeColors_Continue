plugins {
    id("io.papermc.paperweight.userdev")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    implementation(project(":core"))
    paperweight.paperDevBundle("26.1.2.build.19-alpha")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}