package com.campus.lostfound.controller;

import com.campus.lostfound.dao.ItemDAO;
import com.campus.lostfound.dao.ItemDAOMemoryImpl;
import com.campus.lostfound.dao.ItemDAOPostgresImpl;
import com.campus.lostfound.model.Item;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Unified Servlet to handle:
 * POST /api/items/lost
 * POST /api/items/found
 * GET /api/items
 * GET /api/items/search
 */
@WebServlet("/api/items/*")
public class ItemServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ItemDAO itemDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            try {
                itemDAO = new ItemDAOPostgresImpl(databaseUrl);
                System.out.println("Using Postgres DAO");
            } catch (Exception e) {
                System.err.println("Failed to initialize Postgres DAO: " + e.getMessage());
                itemDAO = new ItemDAOMemoryImpl();
            }
        } else {
            itemDAO = new ItemDAOMemoryImpl();
        }
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();

        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
            String type = request.getParameter("type");
            List<Item> items = (type != null && !type.isEmpty()) ? itemDAO.findByType(type) : itemDAO.findAll();
            out.print(gson.toJson(items));
        } else if (pathInfo.equalsIgnoreCase("/search")) {
            String itemName = request.getParameter("itemName");
            String category = request.getParameter("category");
            String location = request.getParameter("location");
            String date = request.getParameter("date");
            List<Item> results = itemDAO.search(itemName, category, location, date);
            out.print(gson.toJson(results));
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\": \"Endpoint not found\"}");
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        try {
            Item item = gson.fromJson(sb.toString(), Item.class);
            if (item == null) {
                throw new Exception("Invalid JSON or empty body");
            }

            if ("/lost".equalsIgnoreCase(pathInfo)) {
                item.setType("LOST");
                item.setStatus("LOST");
            } else if ("/found".equalsIgnoreCase(pathInfo)) {
                item.setType("FOUND");
                item.setStatus("FOUND");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Invalid path for POST. Expected /lost or /found\"}");
                return;
            }

            if (item.getItemName() == null || item.getItemName().trim().isEmpty()) {
                throw new Exception("Item name is required");
            }

            itemDAO.save(item);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().print(gson.toJson(item));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
