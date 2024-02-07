package io.jenkins.plugins.flyway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

@WithJenkins
@Testcontainers(disabledWithoutDocker = true)
public class MssqlSmokeTest {

    public static final String TEST_IMAGE = "mcr.microsoft.com/mssql/server:2022-latest";

    @Test
    public void smokeTest(JenkinsRule j) throws Exception {
        try (MSSQLServerContainer<?> mssql = new MSSQLServerContainer<>(TEST_IMAGE)) {
            mssql.acceptLicense();
            mssql.start();
            mssql.waitingFor(Wait.forListeningPort());
            validateFlywayMigrations(mssql);
            assertTableExists(mssql);
        }
    }

    private void validateFlywayMigrations(MSSQLServerContainer<?> mssql) {
        Flyway flyway = Flyway.configure()
                .dataSource(mssql.getJdbcUrl(), mssql.getUsername(), mssql.getPassword())
                .locations("classpath:io/jenkins/plugins/flyway/migrations/mssql")
                .load();
        flyway.migrate();
    }

    private void assertTableExists(MSSQLServerContainer<?> mssql) throws Exception {
        try (Connection connection =
                        DriverManager.getConnection(mssql.getJdbcUrl(), mssql.getUsername(), mssql.getPassword());
                Statement statement = connection.createStatement()) {
            statement.executeQuery("SELECT TOP 1 1 FROM test");
        }
    }
}
