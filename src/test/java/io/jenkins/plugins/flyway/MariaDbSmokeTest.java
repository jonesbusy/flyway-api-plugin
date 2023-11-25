package io.jenkins.plugins.flyway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.flywaydb.core.Flyway;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class MariaDbSmokeTest {

    public static final String TEST_IMAGE = "mariadb:11.2.2";

    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void smokeTest() throws Exception {
        try (MariaDBContainer<?> mysql = new MariaDBContainer<>(TEST_IMAGE)) {
            mysql.start();
            mysql.waitingFor(Wait.forListeningPort());
            validateFlywayMigrations(mysql);
            assertTableExists(mysql);
        }
    }

    private void validateFlywayMigrations(MariaDBContainer<?> mysql) {
        Flyway flyway = Flyway.configure()
                .dataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword())
                .locations("classpath:io/jenkins/plugins/flyway/migrations/mariadb")
                .load();
        flyway.migrate();
    }

    private void assertTableExists(MariaDBContainer<?> mysql) throws Exception {
        try (Connection connection =
                        DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
                Statement statement = connection.createStatement()) {
            statement.executeQuery("SELECT 1 FROM test LIMIT 1");
        }
    }
}
