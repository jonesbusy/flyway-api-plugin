package io.jenkins.plugins.flyway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import org.flywaydb.core.Flyway;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class MySqlDbSmokeTest {

    public static final String TEST_IMAGE = "mysql:8.2.0";

    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void smokeTest() throws Exception {
        try (MySQLContainer<?> mysql = new MySQLContainer<>(TEST_IMAGE)) {
            mysql.start();
            mysql.waitingFor(Wait.forListeningPort());
            validateFlywayMigrations(mysql);
            assertTableExists(mysql);
        }
    }

    private void validateFlywayMigrations(MySQLContainer<?> mysql) {
        Flyway flyway = Flyway.configure()
                .dataSource(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword())
                .locations("classpath:io/jenkins/plugins/flyway/migrations/mysql")
                .load();
        flyway.migrate();
    }

    private void assertTableExists(MySQLContainer<?> mysql) throws Exception {
        try (Connection connection =
                        DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
                Statement statement = connection.createStatement()) {
            statement.executeQuery("SELECT 1 FROM test LIMIT 1");
        }
    }
}
