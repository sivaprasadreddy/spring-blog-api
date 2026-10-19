package com.sivalabs.blog;

import static com.sivalabs.blog.ArchitectureTests.BASE_PACKAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Service;

@Order(1)
@AnalyzeClasses(packages = BASE_PACKAGE)
class ArchitectureTests {
    public static final String BASE_PACKAGE = "com.sivalabs.blog";

    @ArchTest
    static final ArchRule mappersShouldNotBePublic =
            classes().that().haveNameMatching(".*Mapper").should().notBePublic();

    @ArchTest
    static final ArchRule entitiesShouldNotBePublic =
            classes().that().areAnnotatedWith(Entity.class).should().notBePublic();

    @ArchTest
    static final ArchRule controllersShouldNotBeAccessedByDomain = noClasses()
            .that()
            .resideInAPackage("..domain..")
            .should()
            .accessClassesThat()
            .resideInAPackage("..api..");

    // Services are public (accessible to inbound handlers in the same module),
    // but must only be accessed from within their own module — other modules must go through *API facades.
    @ArchTest
    static final ArchRule servicesShouldNotBeUsedFromOtherModules = classes()
            .that()
            .areAnnotatedWith(Service.class)
            .should(new ArchCondition<>("only be accessed from same module") {
                @Override
                public void check(JavaClass service, ConditionEvents events) {
                    String serviceModule = moduleOf(service);
                    for (Dependency dependency : service.getDirectDependenciesToSelf()) {
                        JavaClass origin = dependency.getOriginClass();
                        String originModule = moduleOf(origin);
                        if (!serviceModule.equals(originModule)) {
                            String message =
                                    origin.getName() + " accesses " + service.getName() + " from another module";
                            events.add(SimpleConditionEvent.violated(dependency, message));
                        }
                    }
                }
            });

    private static String moduleOf(JavaClass clazz) {
        String pkg = clazz.getPackageName();
        String[] parts = pkg.split("\\.");
        return parts[BASE_PACKAGE.split("\\.").length];
    }
}
