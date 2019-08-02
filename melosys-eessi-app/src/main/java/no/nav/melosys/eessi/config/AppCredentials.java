package no.nav.melosys.eessi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "melosys.systemuser")
public class AppCredentials {

  private String username;
  private String password;
}
