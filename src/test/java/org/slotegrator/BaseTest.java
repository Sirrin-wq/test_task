package org.slotegrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;
import org.slotegrator.clients.ApiClient;
import org.slotegrator.config.ApiConfig;
import org.slotegrator.dto.automationTask.PlayerResponseDTO;
import org.slotegrator.steps.PlayerStep;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseTest {
    
    protected static ApiConfig config;
    protected static ApiClient apiClient;
    protected static ObjectMapper objectMapper;
    protected static PlayerStep playerStep;

    @BeforeAll
    static void setUpBase() {
        config = ConfigFactory.create(ApiConfig.class);
        apiClient = new ApiClient(config);
        objectMapper = new ObjectMapper();
        playerStep = new PlayerStep();
    }
    
    /**
     * Парсит ответ в список игроков.
     * Пытается распарсить как массив, если не получается - как одиночный объект.
     * Если не удается распарсить - возвращает пустой список.
     * 
     * @param response ответ от API
     * @return список игроков или пустой список при ошибке парсинга
     */
    protected List<PlayerResponseDTO> parsePlayersResponse(Response response) {
        String responseBody = response.getBody().asString();
        try {
            return objectMapper.readValue(responseBody, new TypeReference<List<PlayerResponseDTO>>() {});
        } catch (Exception e) {
            try {
                PlayerResponseDTO singlePlayer = objectMapper.readValue(responseBody, PlayerResponseDTO.class);
                return List.of(singlePlayer);
            } catch (Exception ex) {
                if (responseBody == null || responseBody.trim().isEmpty() || responseBody.equals("{}")) {
                    return new ArrayList<>();
                }
                return new ArrayList<>();
            }
        }
    }
}
