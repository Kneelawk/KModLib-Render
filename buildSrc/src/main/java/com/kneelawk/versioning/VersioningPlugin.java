package com.kneelawk.versioning;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;

@SuppressWarnings("unused")
public class VersioningPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        ExtensionContainer ext = target.getExtensions();

        String detectedVersion;
        String releaseTag = System.getenv("RELEASE_TAG");
        if (releaseTag != null) {
            detectedVersion = releaseTag.substring(1);
            System.out.println("Detected release version: " + detectedVersion);
        } else {
            detectedVersion = (String) target.property("local_mod_version");
            System.out.println("Detected local version: " + detectedVersion);
        }

        if (detectedVersion == null) {
            throw new IllegalStateException(
                "Failed to detect version. The `local_mod_version` property must be specified in local builds. The `RELEASE_TAG` environment variable must be specified in release builds.");
        }

        ext.add("mod_version", detectedVersion);
        target.setVersion(detectedVersion);
    }
}
