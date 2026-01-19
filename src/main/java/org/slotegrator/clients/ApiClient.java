package org.slotegrator.clients;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slotegrator.config.ApiConfig;
import org.slotegrator.dto.automationTask.PlayerRequestDTO;
import org.slotegrator.dto.automationTask.PlayerRequestOneDTO;
import org.slotegrator.dto.tester.login.CredentialsDTO;

public class ApiClient {
    private static final String LOGIN_ENDPOINT = "/api/tester/login";
    private static final String CREATE_PLAYER_ENDPOINT = "/api/automationTask/create";
    private static final String GET_PLAYER_ENDPOINT = "/api/automationTask/getOne";
    private static final String GET_ALL_PLAYERS_ENDPOINT = "/api/automationTask/getAll";
    private static final String DELETE_PLAYER_ENDPOINT = "/api/automationTask/deleteOne/{id}";
    
    private final ApiConfig config;
    private final RequestSpecification baseSpec;
    
    public ApiClient(ApiConfig config) {
        this.config = config;
        this.baseSpec = new RequestSpecBuilder()
                .setBaseUri(config.baseUrl())
                .setContentType(ContentType.JSON)
                .build();
    }
    
    /**
     * Получение токена пользователя
     * @param credentials учетные данные
     * @return ответ с токеном
     */
    public Response login(CredentialsDTO credentials) {
        return RestAssured.given()
                .spec(baseSpec)
                .auth()
                .basic(credentials.getEmail(), credentials.getPassword())
                .body(credentials)
                .when()
                .post(LOGIN_ENDPOINT);
    }
    
    /**
     * Регистрация нового игрока
     * @param playerRequest данные игрока
     * @param token токен авторизации
     * @return ответ с данными созданного игрока
     */
    public Response createPlayer(PlayerRequestDTO playerRequest, String token) {
        return RestAssured.given()
                .spec(baseSpec)
                .header("Authorization", "Bearer " + token)
                .body(playerRequest)
                .when()
                .post(CREATE_PLAYER_ENDPOINT);
    }
    
    /**
     * Получение данных игрока по email
     * @param playerRequestOne запрос с email
     * @param token токен авторизации
     * @return ответ с данными игрока
     */
    public Response getPlayer(PlayerRequestOneDTO playerRequestOne, String token) {
        return RestAssured.given()
                .spec(baseSpec)
                .header("Authorization", "Bearer " + token)
                .body(playerRequestOne)
                .when()
                .post(GET_PLAYER_ENDPOINT);
    }
    
    /**
     * Получение всех игроков
     * @param token токен авторизации
     * @return ответ со списком игроков
     */
    public Response getAllPlayers(String token) {
        return RestAssured.given()
                .spec(baseSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(GET_ALL_PLAYERS_ENDPOINT);
    }
    
    /**
     * Удаление игрока по ID
     * @param playerId ID игрока
     * @param token токен авторизации
     * @return ответ об удалении
     */
    public Response deletePlayer(Integer playerId, String token) {
        return RestAssured.given()
                .spec(baseSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", playerId)
                .when()
                .delete(DELETE_PLAYER_ENDPOINT);
    }
}
