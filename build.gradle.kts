plugins {
    id("dev.kikugie.loom-back-compat")
}

version = "${property("mod.version")}+mc${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    else -> JavaVersion.VERSION_21
}

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }

    fun strictMaven(repos: List<String>, vararg groups: String) = exclusiveContent {
        repos.forEach { forRepository { maven(it) } }
        filter { groups.forEach(::includeGroup) }
    }

    strictMaven("https://repo.essential.gg/public", "Essential-Repo", "gg.essential")
}


dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")

    modImplementation("org.apache.httpcomponents:httpcore:4.4.16")
    include("org.apache.httpcomponents:httpcore:4.4.16")

    modImplementation("org.apache.httpcomponents:httpclient:4.5.14")
    include("org.apache.httpcomponents:httpclient:4.5.14")

    modImplementation("commons-logging:commons-logging:1.2")
    include("commons-logging:commons-logging:1.2")

    modImplementation("commons-codec:commons-codec:1.15")
    include("commons-codec:commons-codec:1.15")

    include("gg.essential:partner-mod-integration-${property("deps.essential_partner_mod_integration.minecraft_version")}:${property("deps.essential_partner_mod_integration.version")}")
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1")
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run")
        jvmArguments.add("-Dmixin.debug.export=true")
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

sourceSets {
    val ducks = create("ducks") {
        compileClasspath += sourceSets["main"].compileClasspath
    }

    main {
        compileClasspath += ducks.output
    }
}

tasks {
    jar {
        val projectName = project.name
        inputs.property("projectName", projectName)

        from("LICENSE") {
            rename { "${it}_${projectName}" }
        }
    }

    processResources {
        fun MutableMap<String, String>.register(key: String, property: String) {
            val value: String = sc.properties[property]
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", "mod.id")
            register("name", "mod.name")
            register("version", "mod.version")
            register("minecraft", "mod.mc_compat")
        }

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

        inputs.property("version", project.property("mod.version"))
        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    }
}
