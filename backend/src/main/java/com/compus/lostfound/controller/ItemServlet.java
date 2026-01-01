package com.campus.lostfound.controller;

import com.campus.lostfound.dao.ItemDAO;

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
                com.campus.lostfound.util.DatabaseUtil.init(databaseUrl);
                itemDAO = new ItemDAOPostgresImpl();
                System.out.println("Using Postgres DAO");
            } catch (Exception e) {
                throw new ServletException("Failed to initialize Postgres DAO", e);
            }
        } else {
            throw new ServletException("DATABASE_URL environment variable is required for Neon PostgreSQL. Please configure it.");
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

            // Check Authenticated User
            String userId = item.getUserId();
            if (userId == null || userId.isEmpty()) {
                // Fallback to checking header
                userId = request.getHeader("X-User-Id");
                if (userId != null) item.setUserId(userId);
            }
            
            if (item.getUserId() == null || item.getUserId().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Unauthorized. User ID required.\"}");
                return;
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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String itemId = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        String userId = req.getHeader("X-User-Id");

        if (userId == null || userId.isEmpty()) {
            System.out.println("Delete failed: No X-User-Id header");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\": \"Unauthorized\"}");
            return;
        }
        System.out.println("Processing delete for itemId: " + itemId + " with userId: " + userId);

        try {
            boolean deleted = itemDAO.delete(itemId, userId);
            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"message\": \"Item deleted\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write("{\"error\": \"Item not found or you do not have permission to delete it\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }


}
