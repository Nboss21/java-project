package com.campus.lostfound.dao;

import com.campus.lostfound.model.Item;
import com.campus.lostfound.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PostgreSQL implementation of ItemDAO using the shared DatabaseUtil.
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
        String sql = "CREATE TABLE IF NOT EXISTS items (" +
                "id VARCHAR(64) PRIMARY KEY," +
                "item_name VARCHAR(255) NOT NULL," +
                "category VARCHAR(100)," +
                "description TEXT," +
                "location VARCHAR(255)," +
                "date DATE," +
                "status VARCHAR(50)," +
                "contact_info VARCHAR(255)," +
                "type VARCHAR(20)," +
                "user_id VARCHAR(64)" +
                ")";
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    @Override
    public void save(Item item) {
        if (item.getId() == null || item.getId().isEmpty()) {
            item.setId(UUID.randomUUID().toString());
        }
        String sql = "INSERT INTO items (id, item_name, category, description, location, date, status, contact_info, type, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getId());
            ps.setString(2, item.getItemName());
            ps.setString(3, item.getCategory());
            ps.setString(4, item.getDescription());
            ps.setString(5, item.getLocation());
            if (item.getDate() != null) {
                ps.setDate(6, new Date(item.getDate().getTime()));
            } else {
                ps.setDate(6, null);
            }
            ps.setString(7, item.getStatus());
            ps.setString(8, item.getContactInfo());
            ps.setString(9, item.getType());
            ps.setString(10, item.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving item: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Item> findAll() {
        String sql = "SELECT * FROM items";
        List<Item> list = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Item> findByType(String type) {
        String sql = "SELECT * FROM items WHERE LOWER(type) = LOWER(?)";
        List<Item> list = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Item> search(String itemName, String category, String location, String date) {
        StringBuilder sb = new StringBuilder("SELECT * FROM items WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (itemName != null && !itemName.isEmpty()) {
            sb.append(" AND LOWER(item_name) LIKE ?");
            params.add("%" + itemName.toLowerCase() + "%");
        }
        if (category != null && !category.isEmpty()) {
            sb.append(" AND LOWER(category) = LOWER(?)");
            params.add(category);
        }
        if (location != null && !location.isEmpty()) {
            sb.append(" AND LOWER(location) LIKE ?");
            params.add("%" + location.toLowerCase() + "%");
        }
        if (date != null && !date.isEmpty()) {
            sb.append(" AND CAST(date AS TEXT) LIKE ?");
            params.add("%" + date + "%");
        }

        List<Item> list = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean delete(String itemId, String userId) {
        String sql = "DELETE FROM items WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, itemId);
            ps.setString(2, userId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getString("id"));
        item.setItemName(rs.getString("item_name"));
        item.setCategory(rs.getString("category"));
        item.setDescription(rs.getString("description"));
        item.setLocation(rs.getString("location"));
        Date d = rs.getDate("date");
        if (d != null) {
            item.setDate(new java.util.Date(d.getTime()));
        }
        item.setStatus(rs.getString("status"));
        item.setContactInfo(rs.getString("contact_info"));
        item.setType(rs.getString("type"));
        item.setUserId(rs.getString("user_id"));
        return item;
    }
}
