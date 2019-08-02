package no.nav.melosys.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "no.nav.melosys.utils")
public class KafkaTestProperties {
    private String topicsPattern;
    private Boolean enableAutoCommit;
    private String keyDeserializer;
    private String valueDeserializer;
    private String isolationLevel;
}