package com.thewhite.security;

import com.jupiter.tools.spring.test.postgres.annotation.meta.EnablePostgresIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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