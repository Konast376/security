package com.thewhite;

import com.jupiter.tools.spring.test.postgres.annotation.meta.EnablePostgresIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


/**
 * Created on 20.08.2018.
 *
 * @author Anatolii Korovin
 * @author Sergey Vdovin
 * @author Maxim Seredkin
 */
@EnablePostgresIntegrationTest
class AppIT {

    @Test
    @DisplayName("Интеграционный тест подъема контекста")
    void contextRunTest() { }
}