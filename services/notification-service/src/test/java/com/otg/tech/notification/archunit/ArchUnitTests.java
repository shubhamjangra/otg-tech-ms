package com.otg.tech.notification.archunit;

import com.otg.tech.archunit.AbstractArchUnitTests;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;

@AnalyzeClasses(packages = "com.otg.tech",
        importOptions = {ImportOption.DoNotIncludeTests.class, ImportOption.DoNotIncludeJars.class})
public class ArchUnitTests extends AbstractArchUnitTests {
}
