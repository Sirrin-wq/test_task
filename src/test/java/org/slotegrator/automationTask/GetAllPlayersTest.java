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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.slotegrator.util.Validator.validateStatusCode;

@DisplayName("Тесты получения всех пользователей и сортировки")
class GetAllPlayersTest extends BaseTest {
    
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
    @DisplayName("Позитивный тест: Запрос данных всех пользователей и сортировка по имени")
    void testGetAllPlayersAndSortByName_Success() {
        List<PlayerResponseDTO> createdPlayers = playerStep.createPlayers(apiClient, authToken, 12);
        
        Response response = apiClient.getAllPlayers(authToken);
        validateStatusCode(response, 200);
        List<PlayerResponseDTO> allPlayers = parsePlayersResponse(response);
        
        assertThat(allPlayers)
                .as("Список игроков не должен быть пустым")
                .isNotEmpty();
        
        var createdPlayerIds = createdPlayers.stream()
                .map(PlayerResponseDTO::getId)
                .collect(Collectors.toList());
        
        var allPlayerIds = allPlayers.stream()
                .map(PlayerResponseDTO::getId)
                .collect(Collectors.toList());
        
        assertThat(allPlayerIds)
                .as("Список всех игроков должен содержать ID созданных игроков")
                .containsAll(createdPlayerIds);
        
        List<PlayerResponseDTO> sortedPlayers = allPlayers.stream()
                .sorted(Comparator.comparing(PlayerResponseDTO::getName, Comparator.nullsLast(String::compareTo)))
                .toList();
    }
    
    @Test
    @DisplayName("Негативный тест: Получение всех игроков без авторизации")
    void testGetAllPlayers_Unauthorized() {
        String invalidToken = "invalid_token";
        
        Response response = apiClient.getAllPlayers(invalidToken);
        
        int statusCode = response.getStatusCode();
        assertThat(statusCode)
                .as("HTTP статус код должен быть 401 или ")
                .isIn(401, 403);
    }
}
