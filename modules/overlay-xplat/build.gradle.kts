plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
    id("com.kneelawk.kpublish")
}

submodule {
    setLibsDirectory()
    setRefmaps("kmodlib-overlay")
    applyFabricLoaderDependency()
    forceRemap()
    setupJavadoc()

    val common_events_version: String by project
    xplatExternalDependency { "com.kneelawk.common-events:common-events-main-bus-$it:$common_events_version" }
}

kpublish {
    createPublication("intermediary")
}
