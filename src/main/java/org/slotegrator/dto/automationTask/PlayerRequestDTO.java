package org.slotegrator.dto.automationTask;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRequestDTO {
    private String username;
    
    @JsonProperty("password_change")
    private String passwordChange;
    
    @JsonProperty("password_repeat")
    private String passwordRepeat;
    
    private String email;
    private String name;
    private String surname;
    
    @JsonProperty("currency_code")
    private String currencyCode;
}
