package com.campus.lostfound.controller;

import com.campus.lostfound.dao.UserDAO;
import com.campus.lostfound.dao.UserDAOPostgresImpl;
import com.campus.lostfound.model.User;
import com.google.gson.Gson;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Add webServlet authentication 
@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private UserDAO userDAO;
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        String dbUrl = System.getenv("DATABASE_URL");
        if (dbUrl == null || dbUrl.isEmpty()) {
            throw new ServletException("DATABASE_URL environment variable not set");
        }
        try {
            com.campus.lostfound.util.DatabaseUtil.init(dbUrl);
            userDAO = new UserDAOPostgresImpl(); // No connection passed, it uses DatabaseUtil
        } catch (Exception e) {
            throw new ServletException("Failed to initialize AuthServlet", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if ("/register".equals(path)) {
            handleRegister(req, resp);
        } else if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User newUser = gson.fromJson(req.getReader(), User.class);
            if (newUser.getUsername() == null || newUser.getPassword() == null || newUser.getEmail() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson("Missing field(s)"));
                return;
            }
            
            // Check if exists
            if (userDAO.findByUsername(newUser.getUsername()) != null) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write(gson.toJson("Username already taken"));
                return;
            }
            
            userDAO.createUser(newUser);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson("User created successfully"));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Error: " + e.getMessage()));
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User creds = gson.fromJson(req.getReader(), User.class);
            User user = userDAO.findByUsername(creds.getUsername());
            
            if (user != null && user.getPassword().equals(creds.getPassword())) {
                // Return user info (excluding password)
                user.setPassword(null);
                resp.getWriter().write(gson.toJson(user));
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Invalid credentials"));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Error: " + e.getMessage()));
        }
    }
}

