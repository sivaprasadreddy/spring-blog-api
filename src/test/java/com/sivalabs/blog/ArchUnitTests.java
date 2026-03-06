package com.sivalabs.blog;

import com.enofex.taikai.Taikai;
import com.enofex.taikai.java.ImportsConfigurer;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.List;

import static com.tngtech.archunit.core.domain.JavaModifier.*;

class ArchUnitTests {
    private static final String BASE_PACKAGE = ArchUnitTests.class.getPackageName();

    @Test
    void shouldFulfillConstraints() {
        Taikai.builder()
                .namespace(BASE_PACKAGE)
                .java(java -> java
                        .noUsageOfDeprecatedAPIs()
                        .methodsShouldNotDeclareGenericExceptions()
                        .utilityClassesShouldBeFinalAndHavePrivateConstructor()
                        .imports(ImportsConfigurer::shouldHaveNoCycles)
                        .naming(naming -> naming
                                .fieldsShouldNotMatch(".*(List|Set|Map)$")
                                .constantsShouldFollowConventions()
                                .interfacesShouldNotHavePrefixI()))
                .logging(logging -> logging
                        .loggersShouldFollowConventions(Logger.class, "LOG", List.of(PRIVATE, STATIC, FINAL)))
                .test(test -> test
                        .junit(junit -> junit
                                .classesShouldBePackagePrivate(".*Test(s)")
                                .classesShouldNotBeAnnotatedWithDisabled()
                                .methodsShouldNotBeAnnotatedWithDisabled()))
                .spring(spring -> spring
                        .noAutowiredFields()
                        .boot(boot -> boot
                                .applicationClassShouldResideInPackage(BASE_PACKAGE))
                        .configurations(c -> c.namesShouldMatch(".+Config"))
                        .controllers(controllers -> controllers
                                .shouldBeAnnotatedWithRestController()
                                .namesShouldEndWithController()
                                .shouldNotDependOnOtherControllers()
                                .shouldBePackagePrivate())
                        .services(services -> services
                                //.shouldBeAnnotatedWithService()
                                .shouldNotDependOnControllers()
                                .namesShouldEndWithService())
                        .repositories(repositories -> repositories
                                .shouldNotDependOnServices()
                                .namesShouldEndWithRepository()))
                .build()
                .checkAll();
    }
}
