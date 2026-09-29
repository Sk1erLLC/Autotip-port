plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.3" /* DO NOT EDIT */

stonecutter parameters {
    swaps["mod_id"] = "\"${property("mod.id")}\";"
    swaps["mod_name"] = "\"${property("mod.name")}\";"
    swaps["mod_version"] = "\"${property("mod.version")}\";"
    swaps["minecraft"] = "\"${node.metadata.version}\";"
    dependencies["fapi"] = node.project.property("deps.fabric_api") as String
    dependencies["emiv"] = node.project.property("deps.essential_partner_mod_integration.version") as String
    dependencies["emimcv"] = node.project.property("deps.essential_partner_mod_integration.minecraft_version") as String

    replacements {
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }

        string(current.parsed >= "26.1") {
            replace("classTweaker v2 named", "classTweaker v2 official")
        }
    }
}
