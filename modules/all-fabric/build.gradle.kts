plugins {
    id("com.kneelawk.versioning")
    id("com.kneelawk.submodule")
    id("com.kneelawk.kpublish")
}

submodule {
    setLibsDirectory()
    applyFabricLoaderDependency()
    applyFabricApiDependency()
    setupJavadoc()
}

dependencies {
    api(project(":blockmodel", configuration = "namedElements"))
    include(project(":blockmodel"))

    api(project(":overlay-fabric", configuration = "namedElements"))
    include(project(":overlay-fabric"))

    api(project(":renderlayer", configuration = "namedElements"))
    include(project(":renderlayer"))
}

kpublish {
    createPublication()
}
