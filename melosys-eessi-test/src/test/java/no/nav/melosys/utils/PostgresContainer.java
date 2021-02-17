package no.nav.melosys.utils;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainer extends PostgreSQLContainer<PostgresContainer> {

    private static final String IMAGE_NAME = "postgres:11";
    private static PostgresContainer container;

    public PostgresContainer() {
        super(IMAGE_NAME);
    }

    public static PostgresContainer getInstance() {
        if (container == null) {
            container = new PostgresContainer()
                    .withUsername("postgres")
                    .withPassword("su")
                    .withDatabaseName("postgres");

            container.start();
            System.setProperty("spring.datasource.url", container.getJdbcUrl());
        }

        return container;
    }
    @Override
    public void start() {
        super.start();
    }
}
