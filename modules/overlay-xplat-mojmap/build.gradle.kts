plugins {
    id("com.kneelawk.submodule")
    id("com.kneelawk.versioning")
    id("com.kneelawk.kpublish")
}

submodule {
    applyFabricLoaderDependency()
    applyXplatConnection(":overlay-xplat", "mojmap")
    setupJavadoc()
    disableRemap()
}

dependencies {
    val common_events_version: String by project
    modApi("com.kneelawk.common-events:common-events-events-xplat-mojmap:$common_events_version")
}

kpublish {
    createPublication()
}
