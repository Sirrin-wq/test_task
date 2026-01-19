package org.slotegrator.automationTask;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slotegrator.BaseTest;
import org.slotegrator.dto.automationTask.PlayerRequestOneDTO;
import org.slotegrator.dto.automationTask.PlayerResponseDTO;
import org.slotegrator.dto.tester.login.CredentialsDTO;
import org.slotegrator.dto.tester.login.TokenDTO;
import org.slotegrator.util.TestDataBuilder;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slotegrator.util.AssertMessages.FIELD_SHOULD_MATCH_REQUEST;
import static org.slotegrator.util.Validator.validateStatusCode;

@DisplayName("Тесты получения данных профиля игрока")
class GetPlayerTest extends BaseTest {
    
    private String authToken;
    
    @BeforeEach
    void setUp() {
        CredentialsDTO credentials = TestDataBuilder.createCredentials(
                config.testEmail(), 
                config.testPassword()
        );
        Response loginResponse = apiClient.login(credentials);
        TokenDTO tokenResponse = loginResponse.as(TokenDTO.class);
        authToken = tokenResponse.getAccessToken();
    }
    
    @Test
    @DisplayName("Позитивный тест: Запрос данных профиля созданного игрока")
    void testGetPlayerProfile_Success() {
        var createdPlayer = playerStep.createPlayers(apiClient, authToken, 1).get(0);
        assertThat(createdPlayer.getId())
                .as("Игрок должен быть создан")
                .isNotNull();
        PlayerRequestOneDTO request = TestDataBuilder.createPlayerRequestOne(createdPlayer.getEmail());

        Response response = apiClient.getPlayer(request, authToken);
        
        validateStatusCode(response, 201);
        PlayerResponseDTO playerResponse = response.as(PlayerResponseDTO.class);
        assertThat(playerResponse)
                .as("Ответ должен содержать данные игрока")
                .isNotNull();
        assertThat(playerResponse.getId())
                .as("ID игрока должен быть указан")
                .isEqualTo(createdPlayer.getId());
        assertThat(playerResponse.getEmail())
                .as(FIELD_SHOULD_MATCH_REQUEST.formatted("Email"))
                .isEqualTo(createdPlayer.getEmail());
        assertThat(playerResponse.getUsername())
                .as(FIELD_SHOULD_MATCH_REQUEST.formatted("Username"))
                .isEqualTo(createdPlayer.getUsername());
        assertThat(playerResponse.getName())
                .as(FIELD_SHOULD_MATCH_REQUEST.formatted("Имя"))
                .isEqualTo(createdPlayer.getName());
        assertThat(playerResponse.getSurname())
                .as(FIELD_SHOULD_MATCH_REQUEST.formatted("Фамилия"))
                .isEqualTo(createdPlayer.getSurname());
    }
    
    @ParameterizedTest(name = "Негативный тест: Получение игрока с невалидными данными - email: {0}, ожидаемый статус: {1}")
    @MethodSource("invalidEmailProvider")
    @DisplayName("Негативные тесты: Получение игрока с невалидными данными")
    void testGetPlayer_InvalidData(String email, int expectedStatus) {
        PlayerRequestOneDTO request = TestDataBuilder.createPlayerRequestOne(email);
        
        Response response = apiClient.getPlayer(request, authToken);
        
        validateStatusCode(response, expectedStatus);
    }
    
    private static Stream<Arguments> invalidEmailProvider() {
        return Stream.of(
                Arguments.of("", 400),
                Arguments.of("nonexistent@test.com", 400),
                Arguments.of("invalid-email", 400),
                Arguments.of(null, 400)
        );
    }
}
