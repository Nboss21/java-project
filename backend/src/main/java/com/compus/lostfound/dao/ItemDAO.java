package com.campus.lostfound.dao;

import com.campus.lostfound.model.Item;
import java.util.List;

/**
 * Data Access Object interface for Items.
 */
public interface ItemDAO {
    void save(Item item);
    List<Item> findAll();
    List<Item> findByType(String type);
    List<Item> search(String itemName, String category, String location, String date);
}
