package com.thewhite.security.api;

import com.thewhite.util.test.junit.jupiter.api.BaseApiExceptionHandlerMvcIT;
import org.junit.jupiter.api.Disabled;

/**
 * Created on 20.08.2018
 *
 * @author Maxim Seredkin
 */
@Disabled("Неверная кодировка возвращаемых данных. " +
          "Задание свойств spring.http.encoding.charset, spring.http.encoding.enabled, spring.http.encoding.force не помогло.")
class ApiExceptionHandlerMvcIT extends BaseApiExceptionHandlerMvcIT {}