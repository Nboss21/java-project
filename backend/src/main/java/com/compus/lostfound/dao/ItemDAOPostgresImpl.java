package com.campus.lostfound.dao;

import com.campus.lostfound.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * JDBC-based implementation of ItemDAO for PostgreSQL.
 * Accepts a JDBC URL (e.g. jdbc:postgresql://host:port/db?sslmode=require) or a
 * Postgres URL (postgres://user:pass@host:port/db) which will be converted.
 */
public class ItemDAOPostgresImpl implements ItemDAO {
    private Connection sharedConnection;
    private final String jdbcUrl;

    public ItemDAOPostgresImpl(String databaseUrl) throws SQLException {
        // Use java.net.URI to robustly parse postgres URIs and preserve query params.
        String tmpUser = null;
        String tmpPassword = null;
        String tmpJdbc = null;
        
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver not found", e);
        }

        try {
            if (databaseUrl.startsWith("jdbc:postgresql://")) {
                tmpJdbc = databaseUrl;
            } else {
                java.net.URI uri = new java.net.URI(databaseUrl);

                String scheme = uri.getScheme();
                if (scheme == null) {
                    throw new IllegalArgumentException("Invalid database URL: missing scheme");
                }

                // Extract user and password from userInfo if present
                String userInfo = uri.getRawUserInfo();
                if (userInfo != null) {
                    String[] up = userInfo.split(":", 2);
                    String rawUser = up[0];
                    String rawPass = (up.length == 2) ? up[1] : null;

                    tmpUser = java.net.URLDecoder.decode(rawUser, java.nio.charset.StandardCharsets.UTF_8.name());
                    if (rawPass != null) {
                        tmpPassword = java.net.URLDecoder.decode(rawPass, java.nio.charset.StandardCharsets.UTF_8.name());
                    }
                    
                    System.out.println("DEBUG: Raw User: " + rawUser);
                    System.out.println("DEBUG: Decoded User: " + tmpUser);
                    System.out.println("DEBUG: Raw Pass (first 2): " + (rawPass != null && rawPass.length() > 2 ? rawPass.substring(0, 2) : "N/A"));
                    System.out.println("DEBUG: Decoded Pass (first 2): " + (tmpPassword != null && tmpPassword.length() > 2 ? tmpPassword.substring(0, 2) : "N/A"));
                    System.out.println("DEBUG: Decoded Pass (last 2): " + (tmpPassword != null && tmpPassword.length() > 2 ? tmpPassword.substring(tmpPassword.length() - 2) : "N/A"));
                }
                


                String host = uri.getHost();
                int port = uri.getPort();
                String path = uri.getPath(); // like "/dbname"
                String dbName = (path != null && path.length() > 1) ? path.substring(1) : "";
                String query = uri.getQuery(); // preserve existing query params

                StringBuilder sb = new StringBuilder();
                sb.append("jdbc:postgresql://");
                sb.append(host == null ? "" : host);
                if (port != -1) sb.append(":").append(port);
                sb.append("/").append(dbName);
                if (query != null && !query.isEmpty()) {
                    sb.append("?").append(query);
                    if (tmpUser != null) {
                        sb.append("&user=").append(java.net.URLEncoder.encode(tmpUser, "UTF-8"));
                    }
                    if (tmpPassword != null) {
                        sb.append("&password=").append(java.net.URLEncoder.encode(tmpPassword, "UTF-8"));
                    }
                } else {
                    if (tmpUser != null) {
                        sb.append("?user=").append(java.net.URLEncoder.encode(tmpUser, "UTF-8"));
                        if (tmpPassword != null) {
                            sb.append("&password=").append(java.net.URLEncoder.encode(tmpPassword, "UTF-8"));
                        }
                    }
                }

                tmpJdbc = sb.toString();
            }
        } catch (Exception e) {
            throw new SQLException("Failed to parse DATABASE_URL: " + e.getMessage(), e);
        }

        this.jdbcUrl = tmpJdbc;

        // Establish initial connection
        try {
            this.sharedConnection = DriverManager.getConnection(jdbcUrl);
        } catch (SQLException e) {
            System.err.println("Failed to establish initial connection: " + e.getMessage());
            // We don't throw here to allow retry in getConnection()
        }
        
        // Test connection
        try (Connection conn = getConnection()) {
            ensureTable(conn);
        }
    }

    private synchronized Connection getConnection() throws SQLException {
        if (sharedConnection == null || sharedConnection.isClosed() || !sharedConnection.isValid(2)) {
            System.out.println("Re-establishing database connection...");
            try {
                if (sharedConnection != null && !sharedConnection.isClosed()) {
                    try { sharedConnection.close(); } catch (Exception  e) {}
                }
            } catch (Exception e) {} // ignore close errors
            
            sharedConnection = DriverManager.getConnection(jdbcUrl);
        }
        return sharedConnection;
    }

    private void ensureTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS items ("
                + "id VARCHAR(64) PRIMARY KEY,"
                + "item_name TEXT,"
                + "category TEXT,"
                + "description TEXT,"
                + "location TEXT,"
                + "date TIMESTAMP,"
                + "status TEXT,"
                + "contact_info TEXT,"
                + "type TEXT"
                + ")";
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    @Override
    public void save(Item item) {
        if (item.getId() == null || item.getId().isEmpty()) {
            item.setId(UUID.randomUUID().toString());
        }

        String sql = "INSERT INTO items (id, item_name, category, description, location, date, status, contact_info, type) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, item.getId());
                ps.setString(2, item.getItemName());
                ps.setString(3, item.getCategory());
                ps.setString(4, item.getDescription());
                ps.setString(5, item.getLocation());
                if (item.getDate() != null) {
                    ps.setTimestamp(6, new Timestamp(item.getDate().getTime()));
                } else {
                    ps.setTimestamp(6, new Timestamp(new Date().getTime()));
                }
                ps.setString(7, item.getStatus());
                ps.setString(8, item.getContactInfo());
                ps.setString(9, item.getType());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving item to Postgres: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Item> findAll() {
        String sql = "SELECT * FROM items ORDER BY date DESC";
        try {
            Connection conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching items: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Item> findByType(String type) {
        String sql = "SELECT * FROM items WHERE type = ? ORDER BY date DESC";
        try {
            Connection conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, type);
                try (ResultSet rs = ps.executeQuery()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching items by type: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Item> search(String itemName, String category, String location, String date) {
        StringBuilder sb = new StringBuilder("SELECT * FROM items WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (itemName != null && !itemName.isEmpty()) {
            sb.append(" AND item_name ILIKE ?");
            params.add("%" + itemName + "%");
        }
        if (category != null && !category.isEmpty()) {
            sb.append(" AND category = ?");
            params.add(category);
        }
        if (location != null && !location.isEmpty()) {
            sb.append(" AND location ILIKE ?");
            params.add("%" + location + "%");
        }
        if (date != null && !date.isEmpty()) {
            // Expecting yyyy-MM-dd; we'll compare date portion
            sb.append(" AND to_char(date, 'YYYY-MM-DD') = ?");
            params.add(date);
        }

        sb.append(" ORDER BY date DESC");

        try {
            Connection conn = getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching items: " + e.getMessage(), e);
        }
    }

    private List<Item> mapResultSet(ResultSet rs) throws SQLException {
        List<Item> items = new ArrayList<>();
        while (rs.next()) {
            Item it = new Item();
            it.setId(rs.getString("id"));
            it.setItemName(rs.getString("item_name"));
            it.setCategory(rs.getString("category"));
            it.setDescription(rs.getString("description"));
            it.setLocation(rs.getString("location"));
            Timestamp ts = rs.getTimestamp("date");
            if (ts != null) it.setDate(new Date(ts.getTime()));
            it.setStatus(rs.getString("status"));
            it.setContactInfo(rs.getString("contact_info"));
            it.setType(rs.getString("type"));
            items.add(it);
        }
        return items;
    }
}
