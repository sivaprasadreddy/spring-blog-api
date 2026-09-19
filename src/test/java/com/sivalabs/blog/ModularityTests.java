package com.sivalabs.blog;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@Order(2)
class ModularityTests {
    static ApplicationModules modules = ApplicationModules.of(BlogApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        new Documenter(modules).writeDocumentation();
    }
}
