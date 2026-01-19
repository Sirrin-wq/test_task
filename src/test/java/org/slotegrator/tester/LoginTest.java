package org.slotegrator.tester;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slotegrator.BaseTest;
import org.slotegrator.clients.ApiClient;
import org.slotegrator.dto.tester.login.CredentialsDTO;
import org.slotegrator.dto.tester.login.TokenDTO;
import org.slotegrator.util.TestDataBuilder;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slotegrator.util.Validator.validateStatusCode;

@DisplayName("Тесты получения токена пользователя")
class LoginTest extends BaseTest {
    
    @Test
    @DisplayName("Позитивный тест: Получение токена пользователя с валидными данными")
    void testGetUserToken_Success() {
        ApiClient testApiClient = new ApiClient(config);
        CredentialsDTO credentials = TestDataBuilder.createCredentials(
                config.testEmail(), 
                config.testPassword()
        );
        
        Response response = testApiClient.login(credentials);
        
        validateStatusCode(response, 200);
        TokenDTO tokenResponse = response.as(TokenDTO.class);
        assertThat(tokenResponse)
                .as("Ответ должен содержать объект TokenDTO")
                .isNotNull();
        assertThat(tokenResponse.getAccessToken())
                .as("Ответ должен содержать токен пользователя")
                .isNotBlank();
        assertThat(tokenResponse.getTokenType())
                .as("Тип токена должен быть указан")
                .isNotNull();
    }
    
    @ParameterizedTest(name = "Негативный тест: Логин с невалидными данными - email: {0}, password: {1}, ожидаемый статус: {2}")
    @MethodSource("invalidCredentialsProvider")
    @DisplayName("Негативные тесты: Логин с невалидными учетными данными")
    void testGetUserToken_InvalidCredentials(String email, String password, int expectedStatus) {
        ApiClient testApiClient = new ApiClient(config);
        CredentialsDTO credentials = TestDataBuilder.createCredentials(email, password);
        
        Response response = testApiClient.login(credentials);
        
        validateStatusCode(response, expectedStatus);
    }
    
    private static Stream<Arguments> invalidCredentialsProvider() {
        return Stream.of(
                Arguments.of("", "test1234", 400),
                Arguments.of("test@example.com", "", 400),
                Arguments.of(null, "test1234", 400),
                Arguments.of("test@example.com", null, 400),
                Arguments.of("invalid@email.com", "wrongpassword", 401),
                Arguments.of("test@example.com", "123", 400),
                Arguments.of("notanemail", "test1234", 400)
        );
    }
}
