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
}

dependencies {
    val common_events_version: String by project
    modApi("com.kneelawk.common-events:common-events-events-xplat-intermediary:$common_events_version")
}

kpublish {
    createPublication("intermediary")
}
