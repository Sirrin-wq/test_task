package org.slotegrator.util;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

@UtilityClass
public class Validator {

    /**
     * Валидирует статус код ответа
     *
     * @param response ответ от API
     * @param expectedStatusCode ожидаемый статус код
     */
    public static void validateStatusCode(Response response, int expectedStatusCode) {
        Assertions.assertThat(response.getStatusCode())
                .as("HTTP статус код должен быть " + expectedStatusCode)
                .isEqualTo(expectedStatusCode);
    }

    /**
     * Валидирует статус код ответа с кастомным сообщением
     *
     * @param response ответ от API
     * @param expectedStatusCode ожидаемый статус код
     * @param message сообщение для ассерта
     */
    public static void validateStatusCode(Response response, int expectedStatusCode, String message) {
        Assertions.assertThat(response.getStatusCode())
                .as(message)
                .isEqualTo(expectedStatusCode);
    }
}
