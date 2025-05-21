import com.google.cloud.tools.jib.api.buildplan.ImageFormat
import net.nemerosa.versioning.VersioningExtension
import org.gradle.kotlin.dsl.invoke
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_22
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.dsl.SpringBootExtension
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {

    id("idea")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("net.nemerosa.versioning") version "3.1.0"
    id("com.google.cloud.tools.jib") version "3.4.5"
    id("org.flywaydb.flyway") version "10.20.0"
    id("org.siouan.frontend-jdk21") version "10.0.0"

    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
    kotlin("plugin.jpa") version "2.1.20"

}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.data:spring-data-commons")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.2")

    implementation("org.slf4j:slf4j-api")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.6")
    implementation("com.h2database:h2")
    implementation("org.flywaydb:flyway-core")

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.14.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

configure<SpringBootExtension> {
    buildInfo()
}

configure<IdeaModel> {
    module {
        inheritOutputDirs = true
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_22.toString()
    targetCompatibility = JavaVersion.VERSION_22.toString()

    options.compilerArgs.addAll(
        listOf("--add-modules=jdk.incubator.vector")
    )
}

tasks.withType<KotlinCompile> {

    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JVM_22
        javaParameters = true
    }
}

frontend {
    nodeDistributionProvided.set(false)
    nodeVersion.set("22.11.0")
    nodeInstallDirectory.set(project.layout.projectDirectory.dir("node"))
    corepackVersion.set("latest")

    installScript.set("install")
    assembleScript.set("run build")
    //checkScript.set("run type-check")
    //publishScript.set("run publish")

    packageJsonDirectory.set(project.layout.projectDirectory.dir("src/main/frontend"))
    cacheDirectory.set(project.layout.projectDirectory.dir(".frontend-gradle-plugin"))
}

tasks.register<Copy>("processFrontendResources") {
    // Directory containing the artifacts produced by the frontend project
    val frontendBuildDir = project.layout.projectDirectory.dir("src/main/frontend/dist/spa")
    val frontendResourcesDir = project.layout.buildDirectory.dir("resources/main/static")

    dependsOn(":assembleFrontend")
    from(frontendBuildDir)
    into(frontendResourcesDir)
}

tasks.named<Task>("processResources") {
    dependsOn("processFrontendResources")
}

configure<VersioningExtension> {
    /**
     * Add GitHub CI branch name environment variable
     */
    branchEnv = listOf("GITHUB_REF_NAME")
}

extra {
    val build = getBuild()
    val versioning: VersioningExtension = extensions.getByName<VersioningExtension>("versioning")
    val branch = versioning.info.branch.replace("/", "-")
    val shortCommit = versioning.info.commit.take(8)

    project.extra["build.date-time"] = build.buildDateAndTime
    project.extra["build.date"] = build.formattedBuildDate()
    project.extra["build.time"] = build.formattedBuildTime()
    project.extra["build.revision"] = versioning.info.commit
    project.extra["build.revision.abbreviated"] = shortCommit
    project.extra["build.branch"] = branch
    project.extra["build.user"] = build.userName()

    val containerImageName = "schaka/${project.name}"
    val containerImageTags = mutableSetOf(shortCommit, branch)
    if (branch.startsWith("v")) {
        containerImageTags.add("stable")
    }

    project.extra["jib.image.name"] = containerImageName
    project.extra["jib.image.tags"] = containerImageTags

    val registryImageName = "ghcr.io/${containerImageName}"
    val registryImageTags = containerImageTags.map { "ghcr.io/${containerImageName}:$it" }.toMutableList()

    project.extra["docker.image.name"] = registryImageName
    project.extra["docker.image.version"] = branch
    project.extra["docker.image.source"] = build.projectSourceRoot()
    project.extra["docker.image.tags"] = registryImageTags

    //remove when there's a better way of producing both arm64 and amd64 images
    containerImageTags.add("arm64-$branch")
    registryImageTags.add("ghcr.io/${containerImageName}:amd64-$branch")

}

tasks.withType<BootRun> {
    jvmArgs(
        arrayOf(
            "--add-modules=jdk.incubator.vector",
            "-Dspring.config.additional-location=optional:file:/workspace/application.yaml",
            "-Dsun.jnu.encoding=UTF-8",
            "-Dfile.encoding=UTF-8",
        )
    )
}

jib {
    to {
        image = "ghcr.io/${project.extra["jib.image.name"]}"
        tags = project.extra["jib.image.tags"] as Set<String>

        auth {
            username = System.getenv("USERNAME")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
    from {
        image = "eclipse-temurin:23-jdk-noble"
        auth {
            username = System.getenv("DOCKERHUB_USER")
            password = System.getenv("DOCKERHUB_PASSWORD")
        }
        platforms {
            /*
            platform {
                architecture = "amd64"
                os = "linux"
            }
            */
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    container {
        jvmFlags = listOf(
            "-Dspring.config.additional-location=optional:file:/workspace/application.yaml",
            "-Dsun.jnu.encoding=UTF-8",
            "-Dfile.encoding=UTF-8",
            "--add-modules=jdk.incubator.vector",
            "-Xms256m",
            "-Xmx512m"
        )
        mainClass = "com.github.schaka.accountability.accountabilityApplicationKt"
        ports = listOf("8080")
        format = ImageFormat.Docker // OCI not yet supported
        volumes = listOf("/database", "/workspace")

        labels.set(
            mapOf(
                "org.opencontainers.image.created" to "${project.extra["build.date"]}T${project.extra["build.time"]}",
                "org.opencontainers.image.revision" to project.extra["build.revision"] as String,
                "org.opencontainers.image.version" to project.version as String,
                "org.opencontainers.image.title" to project.name,
                "org.opencontainers.image.authors" to "Schaka <schaka@github.com>",
                "org.opencontainers.image.source" to project.extra["docker.image.source"] as String,
                "org.opencontainers.image.description" to project.description,
            )
        )

        // Exclude all "developmentOnly" dependencies, e.g. Spring devtools.
        configurationName.set("productionRuntimeClasspath")
    }
}

tasks.withType<BootBuildImage> {

    docker.publishRegistry.url = "ghcr.io"
    docker.publishRegistry.username = System.getenv("USERNAME") ?: "INVALID_USER"
    docker.publishRegistry.password = System.getenv("GITHUB_TOKEN") ?: "INVALID_PASSWORD"

    builder = "paketobuildpacks/builder-jammy-buildpackless-tiny"
    buildpacks = listOf(
        "paketobuildpacks/environment-variables",
        "paketobuildpacks/adoptium",
        "paketobuildpacks/java",
        "paketobuildpacks/health-checker"
    )
    imageName = project.extra["docker.image.name"] as String
    version = project.extra["docker.image.version"] as String
    tags = project.extra["docker.image.tags"] as List<String>
    createdDate = "now"

    // It would also be possible to set this in the graalVmNative block, but we don't want to overwrite Spring's settings
    environment = mapOf(
        "BP_HEALTH_CHECKER_ENABLED" to "true",
        "BPL_JVM_CDS_ENABLED" to "true",
        "BP_JVM_CDS_ENABLED" to "true",
        "CDS_TRAINING_JAVA_TOOL_OPTIONS" to "--add-modules=jdk.incubator.vector -Dspring.profiles.active=cds",
        "BP_JVM_VERSION" to "24",
        "BPE_LANG" to "en_US.UTF-8",
        "BPE_LANGUAGE" to "LANGUAGE=en_US:en",
        "BPE_LC_ALL" to "en_US.UTF-8",
        "BPE_APPEND_JAVA_OPTS" to "--add-modules=jdk.incubator.vector -Xmx512m -Xms256m"
    )
}