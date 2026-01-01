package com.campus.lostfound.dao;

import com.campus.lostfound.model.Item;
import com.campus.lostfound.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * JDBC-based implementation of ItemDAO for PostgreSQL.
 * Uses DatabaseUtil for connection management.
 */
public class ItemDAOPostgresImpl implements ItemDAO {

    public ItemDAOPostgresImpl() {
        try (Connection conn = DatabaseUtil.getConnection()) {
            ensureTable(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                + "type TEXT,"
                + "user_id VARCHAR(64)" 
                + ")";
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
        
        // Add user_id column if it doesn't exist (migration for existing table)
        try (Statement st = conn.createStatement()) {
            try {
                st.execute("ALTER TABLE items ADD COLUMN IF NOT EXISTS user_id VARCHAR(64)");
            } catch (SQLException e) {
                // Ignore if column already exists
            }
        }
    }

    @Override
    public void save(Item item) {
        if (item.getId() == null || item.getId().isEmpty()) {
            item.setId(UUID.randomUUID().toString());
        }

        String sql = "INSERT INTO items (id, item_name, category, description, location, date, status, contact_info, type, user_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
            ps.setString(10, item.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving item to Postgres: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Item> findAll() {
        String sql = "SELECT * FROM items ORDER BY date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching items: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Item> findByType(String type) {
        String sql = "SELECT * FROM items WHERE type = ? ORDER BY date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                return mapResultSet(rs);
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

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                return mapResultSet(rs);
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
            it.setUserId(rs.getString("user_id"));
            items.add(it);
        }
        return items;
    }

    @Override
    public boolean delete(String itemId, String userId) {
        String sql = "DELETE FROM items WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemId);
            ps.setString(2, userId);
            System.out.println("Executing SQL delete for itemId: " + itemId + ", userId: " + userId);
            int rows = ps.executeUpdate();
            System.out.println("Delete rows affected: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting item: " + e.getMessage(), e);
        }
    }
}
