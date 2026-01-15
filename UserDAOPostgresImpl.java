""package com.campus.lostfound.dao;

import com.campus.lostfound.model.User;
import com.campus.lostfound.util.DatabaseUtil;
import java.sql.*;
import java.util.UUID;

public class UserDAOPostgresImpl implements UserDAO {

    public UserDAOPostgresImpl() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            ensureTable(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ensureTable(Connection conn) throws SQLException {
        // Create users table if not exists
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                   + "id VARCHAR(64) PRIMARY KEY,"
                   + "username VARCHAR(255) UNIQUE NOT NULL,"
                   + "password VARCHAR(255) NOT NULL,"
                   + "email VARCHAR(255) UNIQUE NOT NULL"
                   + ")";
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    @Override
    public void createUser(User user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString());
        }
        String sql = "INSERT INTO users (id, username, password, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
            rs.getString("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("email")
        );
    }
}
""