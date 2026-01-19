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
import static org.slotegrator.util.AssertMessages.FIELD_SHOULD_MATCH_REQUEST;
import static org.slotegrator.util.Validator.validateStatusCode;

@DisplayName("Тесты регистрации игроков")
class CreatePlayerTest extends BaseTest {
    
    private String authToken;
    
    @BeforeEach
    void setUp() {
        CredentialsDTO credentials = TestDataBuilder.createCredentials(
                config.testEmail(), 
                config.testPassword()
        );
        Response loginResponse = apiClient.login(credentials);
        authToken = loginResponse.jsonPath().getString("accessToken");
    }
    
    @Test
    @DisplayName("Позитивный тест: Регистрация 12 игроков")
    void testRegisterTwelvePlayers_Success() {
        var playersToCreate = TestDataBuilder.generatePlayers(12);
        
        for (PlayerRequestDTO playerRequest : playersToCreate) {
            Response response = apiClient.createPlayer(playerRequest, authToken);
            
            validateStatusCode(response, 201);
            
            PlayerResponseDTO createdPlayer = response.as(PlayerResponseDTO.class);
            assertThat(createdPlayer)
                    .as("Ответ должен содержать данные созданного игрока")
                    .isNotNull();
            assertThat(createdPlayer.getId())
                    .as("ID игрока должен быть указан")
                    .isNotNull();
            assertThat(createdPlayer.getEmail())
                    .as(FIELD_SHOULD_MATCH_REQUEST.formatted("Email игрока"))
                    .isEqualTo(playerRequest.getEmail());
            assertThat(createdPlayer.getUsername())
                    .as(FIELD_SHOULD_MATCH_REQUEST.formatted("Username игрока"))
                    .isEqualTo(playerRequest.getUsername());
            assertThat(createdPlayer.getName())
                    .as(FIELD_SHOULD_MATCH_REQUEST.formatted("Имя игрока"))
                    .isEqualTo(playerRequest.getName());
            assertThat(createdPlayer.getSurname())
                    .as(FIELD_SHOULD_MATCH_REQUEST.formatted("Фамилия игрока"))
                    .isEqualTo(playerRequest.getSurname());
            assertThat(createdPlayer.getCurrencyCode())
                    .as(FIELD_SHOULD_MATCH_REQUEST.formatted("Код валюты"))
                    .isEqualTo(playerRequest.getCurrencyCode());
        }
    }
    
    @ParameterizedTest(name = "Негативный тест: Регистрация с невалидными данными - поле: {0}, значение: {1}, ожидаемый статус: {2}")
    @MethodSource("invalidPlayerDataProvider")
    @DisplayName("Негативные тесты: Регистрация игрока с невалидными данными")
    void testCreatePlayer_InvalidData(String field, String value, int expectedStatus) {
        var validPlayer = TestDataBuilder.generatePlayers(1).get(0);
        PlayerRequestDTO invalidPlayer = createInvalidPlayer(validPlayer, field, value);
        
        Response response = apiClient.createPlayer(invalidPlayer, authToken);
        
        validateStatusCode(response, expectedStatus,
                "HTTP статус код должен быть %s для поля %s".formatted(expectedStatus,field));
    }
    
    private PlayerRequestDTO createInvalidPlayer(PlayerRequestDTO validPlayer, String field, String value) {
        return switch (field) {
            case "username" -> PlayerRequestDTO.builder()
                    .username(value)
                    .email(validPlayer.getEmail())
                    .passwordChange(validPlayer.getPasswordChange())
                    .passwordRepeat(validPlayer.getPasswordRepeat())
                    .name(validPlayer.getName())
                    .surname(validPlayer.getSurname())
                    .currencyCode(validPlayer.getCurrencyCode())
                    .build();
            case "email" -> PlayerRequestDTO.builder()
                    .username(validPlayer.getUsername())
                    .email(value)
                    .passwordChange(validPlayer.getPasswordChange())
                    .passwordRepeat(validPlayer.getPasswordRepeat())
                    .name(validPlayer.getName())
                    .surname(validPlayer.getSurname())
                    .currencyCode(validPlayer.getCurrencyCode())
                    .build();
            case "password_change" -> PlayerRequestDTO.builder()
                    .username(validPlayer.getUsername())
                    .email(validPlayer.getEmail())
                    .passwordChange(value)
                    .passwordRepeat(validPlayer.getPasswordRepeat())
                    .name(validPlayer.getName())
                    .surname(validPlayer.getSurname())
                    .currencyCode(validPlayer.getCurrencyCode())
                    .build();
            case "password_repeat" -> PlayerRequestDTO.builder()
                    .username(validPlayer.getUsername())
                    .email(validPlayer.getEmail())
                    .passwordChange(validPlayer.getPasswordChange())
                    .passwordRepeat(value)
                    .name(validPlayer.getName())
                    .surname(validPlayer.getSurname())
                    .currencyCode(validPlayer.getCurrencyCode())
                    .build();
            case "name" -> PlayerRequestDTO.builder()
                    .username(validPlayer.getUsername())
                    .email(validPlayer.getEmail())
                    .passwordChange(validPlayer.getPasswordChange())
                    .passwordRepeat(validPlayer.getPasswordRepeat())
                    .name(value)
                    .surname(validPlayer.getSurname())
                    .currencyCode(validPlayer.getCurrencyCode())
                    .build();
            case "surname" -> PlayerRequestDTO.builder()
                    .username(validPlayer.getUsername())
                    .email(validPlayer.getEmail())
                    .passwordChange(validPlayer.getPasswordChange())
                    .passwordRepeat(validPlayer.getPasswordRepeat())
                    .name(validPlayer.getName())
                    .surname(value)
                    .currencyCode(validPlayer.getCurrencyCode())
                    .build();
            case "currency_code" -> PlayerRequestDTO.builder()
                    .username(validPlayer.getUsername())
                    .email(validPlayer.getEmail())
                    .passwordChange(validPlayer.getPasswordChange())
                    .passwordRepeat(validPlayer.getPasswordRepeat())
                    .name(validPlayer.getName())
                    .surname(validPlayer.getSurname())
                    .currencyCode(value)
                    .build();
            default -> validPlayer;
        };
    }
    
    private static Stream<Arguments> invalidPlayerDataProvider() {
        return Stream.of(
                Arguments.of("username", "", 400),
                Arguments.of("username", "abc", 400),
                Arguments.of("email", "", 400),
                Arguments.of("email", "invalid-email", 400),
                Arguments.of("password_change", "", 400),
                Arguments.of("password_change", "123", 400),
                Arguments.of("password_repeat", "DifferentPassword123!", 400),
                Arguments.of("name", "", 400),
                Arguments.of("surname", "", 400),
                Arguments.of("currency_code", "", 400),
                Arguments.of("currency_code", "INVALID", 400)
        );
    }
}
