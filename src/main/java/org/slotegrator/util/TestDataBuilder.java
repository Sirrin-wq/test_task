package org.slotegrator.util;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;
import org.slotegrator.dto.automationTask.PlayerRequestDTO;
import org.slotegrator.dto.automationTask.PlayerRequestOneDTO;
import org.slotegrator.dto.tester.login.CredentialsDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@UtilityClass
public class TestDataBuilder {
    private static final Faker faker = new Faker(new Locale("en"));

    /**
     * Создание учетных данных для входа
     */
    public CredentialsDTO createCredentials(String email, String password) {
        return CredentialsDTO.builder()
                .email(email)
                .password(password)
                .build();
    }
    
    /**
     * Создание запроса на регистрацию игрока
     */
    public PlayerRequestDTO createPlayerRequest(
            String username, String email, String name, String surname, 
            String password, String currencyCode) {
        return PlayerRequestDTO.builder()
                .username(username)
                .email(email)
                .name(name)
                .surname(surname)
                .passwordChange(password)
                .passwordRepeat(password)
                .currencyCode(currencyCode)
                .build();
    }
    
    /**
     * Создание запроса на получение игрока по email
     */
    public PlayerRequestOneDTO createPlayerRequestOne(String email) {
        return PlayerRequestOneDTO.builder()
                .email(email)
                .build();
    }
    
    /**
     * Генерация списка из уникальных игроков
     */
    public List<PlayerRequestDTO> generatePlayers(int count) {
        List<PlayerRequestDTO> players = new ArrayList<>();
        List<String> usedEmails = new ArrayList<>();
        List<String> usedUsernames = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            String username = generateUniqueUsername(usedUsernames);
            String email = generateUniqueEmail(usedEmails);
            String name = faker.name().firstName();
            String surname = faker.name().lastName();
            String password = "Password123!";
            String currencyCode = faker.currency().code();
            
            players.add(createPlayerRequest(username, email, name, surname, password, currencyCode));
            usedEmails.add(email);
            usedUsernames.add(username);
        }
        
        return players;
    }
    
    /**
     * Генерация уникального username
     */
    private String generateUniqueUsername(List<String> usedUsernames) {
        String username;
        do {
            username = faker.name().username().toLowerCase().replaceAll("[^a-z0-9]", "") 
                    + "_" + System.currentTimeMillis() 
                    + "_" + faker.random().nextInt(1000, 9999);
        } while (usedUsernames.contains(username));
        return username;
    }
    
    /**
     * Генерация уникального email
     */
    private String generateUniqueEmail(List<String> usedEmails) {
        String email;
        do {
            email = faker.internet().emailAddress().toLowerCase()
                    .replace("@", "_" + System.currentTimeMillis() + "_@");
        } while (usedEmails.contains(email));
        return email;
    }
}
