// Generated by delombok at Thu Jul 04 12:27:09 CEST 2024
package no.nav.melosys.eessi.config;

import jakarta.persistence.EntityManagerFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Profile("nais")
@Configuration
@EnableJpaRepositories(basePackages = "no.nav.melosys.eessi")
public class DatabaseConfig {
    private static final String PROD_MOUNT_PATH = "postgresql/prod-fss";
    private static final String PREPROD_MOUNT_PATH = "postgresql/preprod-fss";
    @Value("${NAIS_CLUSTER_NAME}")
    private String cluster;
    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${DATABASE_NAME}")
    private String databaseName;

    @Bean
    public DataSource adminDataSource() {
        return dataSource("admin");
    }

    @Primary
    @Bean
    public DataSource userDataSource() {
        return dataSource("user");
    }

    @Bean
    public FlywayConfigurationCustomizer flywayConfig(@Qualifier("adminDataSource") DataSource adminDataSource) {
        return config -> config.initSql(String.format("SET ROLE \"%s-admin\"", databaseName)).dataSource(adminDataSource);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(userDataSource());
        entityManagerFactoryBean.setPackagesToScan("no.nav.melosys.eessi");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.POSTGRESQL);
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
        return entityManagerFactoryBean;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    private HikariDataSource dataSource(String userType) {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setMaximumPoolSize(3);
            config.setMinimumIdle(1);
            String mountPath = isProduction() ? PROD_MOUNT_PATH : PREPROD_MOUNT_PATH;
            String dbRole = String.join("-", databaseName, userType);
            return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, mountPath, dbRole);
        } catch (final java.lang.Throwable $ex) {
            throw lombok.Lombok.sneakyThrow($ex);
        }
    }

    private boolean isProduction() {
        return cluster != null && cluster.equalsIgnoreCase("prod-fss");
    }
}
