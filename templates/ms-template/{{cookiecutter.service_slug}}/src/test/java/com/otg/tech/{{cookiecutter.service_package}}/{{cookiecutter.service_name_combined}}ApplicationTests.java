package com.otg.tech.

{{cookiecutter.service_package}};

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") class {{cookiecutter.service_name_combined}}

ApplicationTests {

    @Test
    void contextLoads () {
    }

}
