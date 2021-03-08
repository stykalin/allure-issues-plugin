description = "Allure Issues Plugin"

plugins {
    `java-library`
    distribution
}

val allureVersion = "2.13.8"
val junitVersion = "5.7.0-M1"
val assertjVersion = "3.18.1"

version = allureVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.qameta.allure:allure-plugin-api:${allureVersion}")
    testImplementation("org.assertj:assertj-core:${assertjVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter:${junitVersion}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to allureVersion
            )
        )
    }
}

val pluginDir = "$buildDir/plugin"
val pluginName = "issues-plugin"
val pluginArchiveName = "$pluginName.zip"

tasks.register<Zip>("zipPlugin") {
    dependsOn("jar")
    archiveFileName.set(pluginArchiveName)
    destinationDirectory.set(file(pluginDir))

    from("$buildDir/libs") {
        include("**.jar")
    }
    from("src/dist/") {
        include("**")
    }
}

tasks.register<Copy>("addToAllure") {
    if (project.hasProperty("dir")) {
        val allureDir = project.property("dir") as String
        dependsOn("zipPlugin")
        from(zipTree("$pluginDir/$pluginArchiveName"))
        into("$allureDir/plugins/$pluginName")

        val allureFile = file("$allureDir/config/allure.yml")
        val text = allureFile.readText()
        val replacedText = text.replace("plugins:", "plugins:\n  - $pluginName")
        allureFile.writeText(replacedText)
        println("Congratulations! $pluginName added successfully. Joy!")
    } else {
        throw RuntimeException("Please provide allure directory path as parameter. For example: gradle addToAllure -Pdir=\"C:\\allure-2.13.8\"")
    }
}