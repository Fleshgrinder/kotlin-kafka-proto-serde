import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.LinkMapping
import org.jetbrains.dokka.gradle.PackageOptions
import org.jetbrains.dokka.DokkaConfiguration.ExternalDocumentationLink
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.2.71"
    id("org.jetbrains.dokka") version "0.9.17"
}

description = "Kafka Serde for Proto"
group = "com.fleshgrinder"
version = "1.0.0"

fun v(name: String): String =
    project.property(name).toString()

fun v(name: String, default: String): String =
    project.findProperty(name)?.toString() ?: default

dependencies {
    api("com.google.protobuf:protobuf-java:${v("com.google.protobuf.version", "[2.5,)")}")
    api("org.apache.kafka:kafka-clients:${v("org.apache.kafka.version", "[0.10,)")}")

    implementation(kotlin("stdlib"))

    val junitVersion = v("org.junit.jupiter.version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

repositories {
    jcenter()
}

tasks.remove(tasks["javadoc"])

val dokka by tasks.getting(DokkaTask::class) {
    outputDirectory = "$buildDir/javadoc"
    includes = listOf("README.md")
    jdkVersion = 8
    linkMapping(delegateClosureOf<LinkMapping> {
        dir = "src/main/kotlin"
        url = "https://github.com/Fleshgrinder/kotlin-kafka-proto-serde/blob/master/src/main/kotlin"
        suffix = "#L"
    })
    packageOptions(delegateClosureOf<PackageOptions> {
        prefix = "org.apache.kafka"
        suppress = true
    })
    packageOptions(delegateClosureOf<PackageOptions> {
        prefix = "com.google.protobuf"
        suppress = true
    })
    externalDocumentationLink(delegateClosureOf<ExternalDocumentationLink.Builder> {
        url = URL("https://kafka.apache.org/20/javadoc/")
        packageListUrl = URL("https://kafka.apache.org/20/javadoc/package-list")
    })
    externalDocumentationLink(delegateClosureOf<ExternalDocumentationLink.Builder> {
        url = URL("https://developers.google.com/protocol-buffers/docs/reference/java/")
        packageListUrl = URL("https://developers.google.com/protocol-buffers/docs/reference/java/package-list")
    })
}

val docs by tasks.registering(Copy::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Copies the Dokka generated documentation into the `/docs` directory for GitHub publishing."
    dependsOn("cleanDocs", dokka)
    from("${dokka.outputDirectory}/style.css")
    from("${dokka.outputDirectory}/${dokka.moduleName}") {
        eachFile {
            println(path)
            if (path == "package-list") {
                filter {
                    if (it.startsWith('$')) ""
                    else it
                }
            } else {
                filter {
                    if (it.contains("../style.css")) it.replace("../style.css", "./style.css")
                    else it
                }
            }
        }
    }
    into("$rootDir/docs")
}

val wrapper by tasks.registering(Wrapper::class) {
    gradleVersion = v("org.gradle.version")
    distributionType = Wrapper.DistributionType.ALL
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = true
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    group = "build"
    description = "Assembles a jar archive containing the source files."
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    group = "build"
    description = "Assembles a jar archive containing the source documentation."
    classifier = "javadoc"
    from(dokka)
}

publishing {
    repositories {
        //mavenCentral()
        maven(uri("$buildDir/repository"))
    }
    publications {
        register("mavenJava", MavenPublication::class.java) {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
        }
    }
}
