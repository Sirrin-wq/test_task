package org.slotegrator.config;

import org.aeonbits.owner.Config;

@Config.Sources({
    "classpath:application.properties",
    "classpath:application-${env}.properties"
})
public interface ApiConfig extends Config {
    
    @Key("api.base.url")
    @DefaultValue("http://localhost:8080")
    String baseUrl();
    
    @Key("test.email")
    @DefaultValue("test@example.com")
    String testEmail();
    
    @Key("test.password")
    @DefaultValue("test1234")
    String testPassword();
}
