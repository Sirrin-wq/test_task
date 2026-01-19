package org.slotegrator.automationTask;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slotegrator.BaseTest;
import org.slotegrator.dto.automationTask.PlayerResponseDTO;
import org.slotegrator.dto.tester.login.CredentialsDTO;
import org.slotegrator.dto.tester.login.TokenDTO;
import org.slotegrator.util.TestDataBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slotegrator.util.Validator.validateStatusCode;

@DisplayName("Тесты проверки пустого списка пользователей")
class GetAllPlayersEmptyTest extends BaseTest {
    
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
    @DisplayName("Позитивный тест: Проверка что список всех пользователей пустой")
    void testGetAllPlayersShouldBeEmpty_Success() {
        Response getAllResponse = apiClient.getAllPlayers(authToken);
        validateStatusCode(getAllResponse, 200);
        
        List<PlayerResponseDTO> existingPlayers = parsePlayersResponse(getAllResponse);

        if (!existingPlayers.isEmpty()) {
            playerStep.deletePlayers(apiClient, authToken, existingPlayers);
            List<PlayerResponseDTO> allPlayers = parsePlayersResponse(apiClient.getAllPlayers(authToken));
            assertThat(allPlayers)
                    .as("Список всех пользователей должен быть пустым")
                    .isEmpty();
        }
        assertThat(existingPlayers)
                .as("Список всех пользователей должен быть пустым")
                .isEmpty();
    }
    
    @Test
    @DisplayName("Негативный тест: Получение списка игроков без авторизации")
    void testGetAllPlayersEmpty_Unauthorized() {
        String invalidToken = "invalid_token";
        
        Response response = apiClient.getAllPlayers(invalidToken);
        
        int statusCode = response.getStatusCode();
        assertThat(statusCode)
                .as("HTTP статус код должен быть 401 или 403")
                .isIn(401, 403);
    }
}
