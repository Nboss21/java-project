package com.campus.lostfound.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple utility for managing a single PostgreSQL connection configuration
 * based on the DATABASE_URL style value (e.g. from Neon).
 */
public class DatabaseUtil {

    private static String jdbcUrl;
    private static String username;
    private static String password;

    /**
     * Initialize connection settings from a Postgres URL such as
     * postgresql://user:pass@host/db?sslmode=require
     */
    public static void init(String databaseUrl) throws URISyntaxException {
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            throw new IllegalArgumentException("databaseUrl must not be null or empty");
        }

        URI uri = new URI(databaseUrl);

        String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            String[] parts = userInfo.split(":", 2);
            username = parts[0];
            password = parts.length > 1 ? parts[1] : null;
        }

        String scheme = uri.getScheme();
        if (scheme == null || !scheme.toLowerCase().startsWith("postgres")) {
            throw new IllegalArgumentException("Unsupported database scheme: " + scheme);
        }

        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getPath();
        String dbName = (path != null && path.length() > 1) ? path.substring(1) : "";
        String query = uri.getQuery();

        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:postgresql://");
        sb.append(host != null ? host : "localhost");
        if (port > 0) {
            sb.append(":").append(port);
        }
        if (dbName != null && !dbName.isEmpty()) {
            sb.append("/").append(dbName);
        }
        if (query != null && !query.isEmpty()) {
            sb.append("?").append(query);
        }

        jdbcUrl = sb.toString();
    }

    public static Connection getConnection() throws SQLException {
        if (jdbcUrl == null) {
            throw new IllegalStateException("DatabaseUtil not initialized. Call init(databaseUrl) first.");
        }
        if (username != null) {
            return DriverManager.getConnection(jdbcUrl, username, password);
        }
        return DriverManager.getConnection(jdbcUrl);
    }
}
