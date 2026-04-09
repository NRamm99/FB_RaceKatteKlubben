package dk.race.racekatteklubben.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {

    private final String url;
    private final String user;
    private final String password;

    public DatabaseConfig() {
        Properties props = new Properties();

        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                        "Mangler db.properties i src/main/resources. " +
                                "Kopiér db.properties.example til db.properties og udfyld værdierne."
                );
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke læse db.properties", e);
        }

        this.url = require(props, "db.url");
        this.user = require(props, "db.user");
        this.password = require(props, "db.password");
    }

    private String require(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("db.properties mangler værdi for: " + key);
        }
        return value.trim();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }
}
