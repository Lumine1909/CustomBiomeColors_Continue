repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":nms:nms_1_20_5"))
    implementation(project(":nms:nms_1_21"))
    implementation(project(":nms:nms_1_21_3"))
    implementation(project(":nms:nms_1_21_5"))
    implementation(project(":nms:nms_1_21_9"))
    implementation(project(":nms:nms_1_21_11"))
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.52"))
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }
}
