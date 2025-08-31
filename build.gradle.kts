import net.nemerosa.versioning.VersioningExtension
import org.gradle.kotlin.dsl.invoke
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.springframework.boot.gradle.dsl.SpringBootExtension
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("idea")
    id("java")
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("net.nemerosa.versioning") version "3.1.0"
    id("org.siouan.frontend-jdk21") version "10.0.0"
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.data:spring-data-commons")
    implementation("com.github.ben-manes.caffeine:caffeine")


    implementation("org.slf4j:slf4j-api")
    implementation("com.h2database:h2")
    implementation("org.flywaydb:flyway-core")

    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")

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

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_24.toString()
    targetCompatibility = JavaVersion.VERSION_24.toString()
}

frontend {
    nodeDistributionProvided.set(false)
    nodeVersion.set("22.11.0")
    nodeInstallDirectory.set(project.layout.projectDirectory.dir("node"))
    corepackVersion.set("latest")

    installScript.set("install")
    assembleScript.set("run build")
    //checkScript.set("run type-check")
    //publishScript.set("run pu/assets/index-BC9fblish")

    packageJsonDirectory.set(project.layout.projectDirectory.dir("src/main/frontend"))
    cacheDirectory.set(project.layout.projectDirectory.dir(".frontend-gradle-plugin"))
}

tasks.register<Copy>("processFrontendResources") {
    // Directory containing the artifacts produced by the frontend project
    //machine builds frontend into one JS file and one HTML file, then goes into building backend
    //spring interprets these files as optional static files that it needs to serve
    //in "resources", you could also have a static folder for images if you want to.
    //https://www.baeldung.com/spring-mvc-static-resources
    //it copies the built frontend into the java build
    //spring has web server tomcat. if we want to serve static resources like HTML files, they need to be
    //put inside the static folder for spring.
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
    val versioning: VersioningExtension = extensions.getByName<VersioningExtension>("versioning")
    val branch = versioning.info.branch.replace("/", "-")
    val shortCommit = versioning.info.commit.take(8)

    project.extra["build.revision"] = versioning.info.commit
    project.extra["build.revision.abbreviated"] = shortCommit
    project.extra["build.branch"] = branch

    val containerImageName = "holly394/${project.name}"
    val containerImageTags = mutableSetOf(shortCommit, branch)
    if (branch.startsWith("v")) {
        containerImageTags.add("stable")
    }

    val registryImageName = "ghcr.io/${containerImageName}"
    val registryImageTags = containerImageTags.map { "ghcr.io/${containerImageName}:$it" }.toMutableList()

    project.extra["docker.image.name"] = registryImageName
    project.extra["docker.image.version"] = branch
    project.extra["docker.image.tags"] = registryImageTags

}

tasks.withType<BootRun> {
    jvmArgs(
        arrayOf(
            "-Dspring.config.additional-location=optional:file:/workspace/application.yaml",
            "-Dsun.jnu.encoding=UTF-8",
            "-Dfile.encoding=UTF-8",
        )
    )
}

//all Docker related things are only in this section

tasks.withType<BootBuildImage> {

    //ghcr = github container registry
    docker.publishRegistry.url = "ghcr.io"

    //authenticate with github account
    //environment var available in the context of a github pipeline/workflow
    docker.publishRegistry.username = System.getenv("USERNAME") ?: "INVALID_USER"
    docker.publishRegistry.password = System.getenv("GITHUB_TOKEN") ?: "INVALID_PASSWORD"

    //like skeleton of Docker container (this is small one that doesn't have ls command)
    builder = "paketobuildpacks/builder-jammy-buildpackless-tiny"

    //software that has instructions for building java, spring image, etc.
    //adoptium is specific jdk => temurin/eclipse sdk
    buildpacks = listOf(
        "paketobuildpacks/environment-variables",
        "paketobuildpacks/adoptium",
        "paketobuildpacks/java"
    )

    imageName = project.extra["docker.image.name"] as String
    version = project.extra["docker.image.version"] as String
    tags = project.extra["docker.image.tags"] as List<String>
    createdDate = "now"

    //Docker specific dependencies
    //BP (build pack) variables
    //..JAVA_OPTS (how much memory allowed to be used in this application)
    environment = mapOf(
        "BPL_JVM_CDS_ENABLED" to "true",
        "BP_JVM_CDS_ENABLED" to "true",
        "BP_JVM_VERSION" to "24",
        "BPE_LANG" to "en_US.UTF-8",
        "BPE_LANGUAGE" to "LANGUAGE=en_US:en",
        "BPE_LC_ALL" to "en_US.UTF-8",
        "BPE_APPEND_JAVA_OPTS" to "-Xmx256m -Xms128m"
    )
}

//when we run an image tag like  'ghcr.io/holly394/accountability:master' it will start the application
//when application starts, database file will be created and logs, folders and files
//Docker creates a container based off of this image
