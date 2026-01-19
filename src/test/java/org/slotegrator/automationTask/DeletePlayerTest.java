package org.slotegrator.automationTask;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slotegrator.BaseTest;
import org.slotegrator.dto.automationTask.PlayerRequestDTO;
import org.slotegrator.dto.automationTask.PlayerResponseDTO;
import org.slotegrator.dto.tester.login.CredentialsDTO;
import org.slotegrator.dto.tester.login.TokenDTO;
import org.slotegrator.util.TestDataBuilder;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slotegrator.util.Validator.validateStatusCode;

@DisplayName("Тесты удаления пользователей")
class DeletePlayerTest extends BaseTest {
    
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
    @DisplayName("Позитивный тест: Удаление всех ранее созданных пользователей")
    void testDeleteAllCreatedPlayers_Success() {
        Response getAllResponse = apiClient.getAllPlayers(authToken);
        validateStatusCode(getAllResponse, 200);
        
        var playersToDelete = parsePlayersResponse(getAllResponse);
        if (playersToDelete.isEmpty()) {
            playersToDelete = playerStep.createPlayers(apiClient, authToken, 12);
        }

        playerStep.deletePlayers(apiClient, authToken, playersToDelete);
        assertThat(parsePlayersResponse(apiClient.getAllPlayers(authToken)))
                .isEmpty();
    }
    
    @ParameterizedTest(name = "Негативный тест: Удаление игрока - ID: {0}, ожидаемый статус: {1}")
    @MethodSource("invalidPlayerIdProvider")
    @DisplayName("Негативные тесты: Удаление игрока с невалидными данными")
    void testDeletePlayer_InvalidData(Integer playerId, int expectedStatus) {
        Response response = apiClient.deletePlayer(playerId, authToken);
        
        validateStatusCode(response, expectedStatus);
    }
    
    private static Stream<Arguments> invalidPlayerIdProvider() {
        return Stream.of(
                Arguments.of(-1, 400),
                Arguments.of(0, 400),
                Arguments.of(999999, 400),
                Arguments.of(null, 400)
        );
    }
    
    @Test
    @DisplayName("Негативный тест: Удаление игрока без авторизации")
    void testDeletePlayer_Unauthorized() {
        PlayerRequestDTO playerRequest = TestDataBuilder.generatePlayers(1).get(0);
        Response createResponse = apiClient.createPlayer(playerRequest, authToken);
        PlayerResponseDTO createdPlayer = createResponse.as(PlayerResponseDTO.class);
        String invalidToken = "invalid_token";
        
        Response response = apiClient.deletePlayer(createdPlayer.getId(), invalidToken);
        
        int statusCode = response.getStatusCode();
        assertThat(statusCode)
                .as("HTTP статус код должен быть 401 или 403")
                .isIn(401, 403);
    }
}
