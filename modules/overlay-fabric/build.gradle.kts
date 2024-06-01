plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
    id("com.kneelawk.kpublish")
}

submodule {
    setLibsDirectory()
    applyFabricLoaderDependency()
    applyFabricApiDependency()
    applyXplatConnection(":overlay-xplat", "fabric")
    setupJavadoc()
}

dependencies {
    val common_events_version: String by project
    modApi("com.kneelawk.common-events:common-events-events-fabric:$common_events_version")
    include("com.kneelawk.common-events:common-events-events-fabric:$common_events_version")
}

kpublish {
    createPublication()
}
