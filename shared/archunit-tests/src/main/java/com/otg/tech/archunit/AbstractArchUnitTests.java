package com.otg.tech.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.ArchTest;

public class AbstractArchUnitTests {

    @ArchTest
    public void only_post_mappings_are_allowed_in_interface(JavaClasses javaClasses) {
        ArchUnitRules.onlyPostMappingAllowed().check(javaClasses);
    }

    @ArchTest
    public void utils_classes_should_only_have_static_methods(JavaClasses javaClasses) {
        ArchUnitRules.utilClassesMethodsShouldBeStatic().check(javaClasses);
    }

    @ArchTest
    public void utils_classes_should_not_be_injected(JavaClasses javaClasses) {
        ArchUnitRules.utilsClassesShouldNotBeInjected().check(javaClasses);
    }

    @ArchTest
    public void layers_should_be_free_of_cycles(JavaClasses javaClasses) {
        ArchUnitRules.layersShouldBeFreeOfCycles().check(javaClasses);
    }

    @ArchTest
    public void classes_should_not_have_public_fields(JavaClasses javaClasses) {
        ArchUnitRules.noPublicField().check(javaClasses);
    }

    @ArchTest
    public void should_not_call_exception_print_stacktrace(JavaClasses javaClasses) {
        ArchUnitRules.noExceptionPrintStacktrace().check(javaClasses);
    }

    @ArchTest
    public void all_public_methods_in_the_controllers_should_return_API_response_wrappers(JavaClasses javaClasses) {
        ArchUnitRules.allPublicMethodsInConstructorReturnApiResponseWrapper().check(javaClasses);
    }
}
