package no.nav.melosys.eessi.config;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import no.nav.melosys.eessi.vault.HikariCPVaultUtil;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Profile("nais")
@Configuration
@EnableJpaRepositories(basePackages = "no.nav.melosys.eessi")
public class DatabaseConfig {

    private static final String APPLICATION_NAME = "melosys-eessi";

    private final Environment environment;

    public DatabaseConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource adminDataSource() {
        return dataSource("admin");
    }

    @Bean
    public DataSource userDataSource() {
        return dataSource("user");
    }

    @Bean
    public FlywayConfigurationCustomizer flywayConfig() {
        return config ->
                config
                        .initSql(String.format("SET ROLE \"%s-admin\"",
                                environment.getRequiredProperty("DATABASE_NAME")))
                        .dataSource(adminDataSource());
    }

    @SneakyThrows
    private HikariDataSource dataSource(String user) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(environment.getProperty("spring.datasource.url"));
        config.setMaximumPoolSize(4);
        config.setMinimumIdle(1);
        String mountPath = "postgresql/preprod-fss";
        return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, mountPath, dbRole(user));
    }

    private String dbRole(String role) {
        final String namespace = environment.getProperty("NAIS_NAMESPACE");
        if ("p".equalsIgnoreCase(namespace)) {
            return String.join("-", APPLICATION_NAME, role);
        }
        return String.join("-", APPLICATION_NAME, namespace, role);
    }
}