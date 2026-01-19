package org.slotegrator.steps;

import io.restassured.response.Response;
import org.slotegrator.clients.ApiClient;
import org.slotegrator.dto.automationTask.PlayerRequestDTO;
import org.slotegrator.dto.automationTask.PlayerResponseDTO;
import org.slotegrator.util.TestDataBuilder;

import java.util.List;

import static org.slotegrator.util.Validator.validateStatusCode;

public class PlayerStep {
    
    /**
     * Создает указанное количество игроков
     * 
     * @param apiClient клиент API
     * @param authToken токен авторизации
     * @param count количество игроков для создания
     * @return список созданных игроков
     */
    public List<PlayerResponseDTO> createPlayers(ApiClient apiClient, String authToken, int count) {
        List<PlayerRequestDTO> playersToCreate = TestDataBuilder.generatePlayers(count);
        
        return playersToCreate.stream()
                .map(playerRequest -> {
                    Response createResponse = apiClient.createPlayer(playerRequest, authToken);
                    validateStatusCode(createResponse, 201,
                            "HTTP статус код должен быть 201 для игрока: %s".formatted(playerRequest.getEmail()));
                    return createResponse.as(PlayerResponseDTO.class);
                })
                .toList();
    }
    /**
     * Удаляет всех переданных игроков
     *
     * @param apiClient клиент API
     * @param authToken токен авторизации
     * @param listPlayers количество игроков для создания
     */
    public void deletePlayers(ApiClient apiClient, String authToken, List<PlayerResponseDTO> listPlayers) {
        listPlayers.forEach(player -> {
            Response deleteResponse = apiClient.deletePlayer(player.getId(), authToken);
            validateStatusCode(deleteResponse, 200);
        });
    }
}
