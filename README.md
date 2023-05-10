# KModLib

Utilities for all my mods

## Depending On

It is unadvised that you depend on these libraries, as they are currently only vetted for internal use and may have API
changes between minor or patch versions. Multiple incompatible versions may be available for the same Minecraft version,
and it is mods' responsibility to update their dependency on KModLib promptly or risk becoming incompatible with mods
using up-to-date versions of KModLib.

```gradle
repositories {
    maven {
        name 'Kneelawk'
        url 'https://kneelawk.com/maven'
    }
    maven {
        // For No Indium? - depended on by blockmodel module
        name 'Cafeteria'
        url 'https://maven.cafeteria.dev/releases/'
    }
}

dependencies {
    // ...
    modImplementation "com.kneelawk:kmodlib-all:${kmodlib_version}"
    include "com.kneelawk:kmodlib-all:${kmodlib_version}"
    // ...
}
```

It is also possible to depend on individual modules. To do this, replace `kmodlib-all` with `kmodlib-blockmodel` or
`kmodlib-renderlayer` (non-exhaustive). For a full list of all modules, check [Kneelawk's Maven]. However, do note that
some modules, like the `kmodlib-render`, module are not part of the current set of KModLib modules, and are therefore
no-longer being updated.

[Kneelawk's Maven]: https://kneelawk.com/maven#com/kneelawk

## Setting Up a Development Environment

QuiltFlower has been made the default decompiler for this project.

One thing to note is that just running `genSources` directly does not work. Instead, `genSources` must be run
individually for each module that needs it. Use `:genSources` to generate sources for the root project, use
`kmodlib-blockmodel:genSources` to generate sources for the BlockModel module, etc.
