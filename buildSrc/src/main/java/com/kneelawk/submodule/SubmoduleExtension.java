package com.kneelawk.submodule;

import org.gradle.api.provider.Property;

public interface SubmoduleExtension {
    Property<String> getSuffix();

    Property<Boolean> getRoot();
}
