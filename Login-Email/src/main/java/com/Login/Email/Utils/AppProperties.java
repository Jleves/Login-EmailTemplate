package com.Login.Email.Utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
    private Security security = new Security();
    private Mail mail = new Mail();
    private General general = new General();


    @Getter @Setter
    public static class Security {
        private Jwt jwt = new Jwt();
        private boolean enableCors;
    }

    @Getter @Setter
    public static class Jwt {
        private String secret;
        private long accessExpirationMinutes;
        private long refreshExpirationHours;
        private long refreshExpirationMinutes;
    }

    @Getter @Setter
    public static class Mail {
        private String host;
        private int port;
        private String username;
        private String password;
    }

    @Getter @Setter
    public static class General {
        private String appName;
        private String version;
        private String timezone;
    }
}