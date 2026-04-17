package org.nowstart.zunyang.partypanic;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "org.nowstart.zunyang.partypanic",
    importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ImportOption.DoNotIncludeJars.class
    }
)
class CleanArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_outer_layers =
        noClasses().that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..application..",
                "..adapter..",
                "..config.."
            );

    @ArchTest
    static final ArchRule application_should_not_depend_on_adapter_or_config =
        noClasses().that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..adapter..",
                "..config.."
            );

    @ArchTest
    static final ArchRule adapter_in_should_not_depend_on_adapter_out =
        noClasses().that().resideInAPackage("..adapter.in..")
            .should().dependOnClassesThat()
            .resideInAPackage("..adapter.out..");

    @ArchTest
    static final ArchRule adapter_out_should_not_depend_on_adapter_in =
        noClasses().that().resideInAPackage("..adapter.out..")
            .should().dependOnClassesThat()
            .resideInAPackage("..adapter.in..");

    @ArchTest
    static final ArchRule core_layers_should_not_depend_on_frameworks =
        noClasses().that().resideInAnyPackage("..domain..", "..application..")
            .should().dependOnClassesThat()
            .resideInAPackage("com.badlogic.gdx..");

    @ArchTest
    static final ArchRule config_should_not_be_depended_on_from_other_layers =
        noClasses().that()
            .resideOutsideOfPackage("..config..")
            .should().dependOnClassesThat()
            .resideInAPackage("..config..");
}
