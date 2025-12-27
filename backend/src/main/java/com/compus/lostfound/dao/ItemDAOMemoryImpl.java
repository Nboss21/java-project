package com.campus.lostfound.dao;

import com.campus.lostfound.model.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * In-memory implementation of ItemDAO. Data is stored in a static list.
 */
public class ItemDAOMemoryImpl implements ItemDAO {
    private static final List<Item> items = new ArrayList<>();

    @Override
    public void save(Item item) {
        if (item.getId() == null || item.getId().isEmpty()) {
            item.setId(UUID.randomUUID().toString());
        }
        items.add(item);
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items);
    }

    @Override
    public List<Item> findByType(String type) {
        if (type == null) return new ArrayList<>();
        return items.stream()
                .filter(item -> item.getType() != null && item.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String itemName, String category, String location, String date) {
        return items.stream()
                .filter(item -> (itemName == null || itemName.isEmpty() || (item.getItemName() != null && item.getItemName().toLowerCase().contains(itemName.toLowerCase()))))
                .filter(item -> (category == null || category.isEmpty() || (item.getCategory() != null && item.getCategory().equalsIgnoreCase(category))))
                .filter(item -> (location == null || location.isEmpty() || (item.getLocation() != null && item.getLocation().toLowerCase().contains(location.toLowerCase()))))
                .filter(item -> (date == null || date.isEmpty() || (item.getDate() != null && item.getDate().toString().contains(date))))
                .collect(Collectors.toList());
    }
}
