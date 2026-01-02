# Campus Lost & Found Item Tracker System

 📌 Project Overview
A centralized digital system for University students to report and search for lost or found items on campus. This project is built using Advanced Java Programming concepts (Servlets, MVC, DAO) and a generic #React frontend.

 🧠 Technologies Used

#Backend (Java Enterprise)
Java Servlets: Core controller logic.
MongoDB: NoSQL database for flexible data storage.
MVC Architecture: Strict separation of Model, View (React), and Controller.
DAO Pattern: Data Access Object pattern for clean database abstraction.
Singleton Pattern: `MongoDBUtil` for efficient connection management.
Filters: `CORSFilter` to handle cross-origin requests securely.
Gson: For JSON serialization/deserialization.
Apache Tomcat: Web server/container.

#Frontend (React)
React 18: Functional Components & Hooks (`useState`, `useEffect`).
Vite: Next-generation build tool.
Axios: For REST API calls.
CSS Modules/Variables**: Professional branding (Deep Indigo + Soft Cyan).


 🚀 Setup Instructions

Prerequisites
1.  Java JDK 8+ installed.
2.  Apache Tomcat 9/10 installed.
3.  MongoDB running locally on port `27017` (default).
4.  Node.js & npm installed.

 1. Backend Setup
1.  Open the `backend` folder in your IDE (IntelliJ/Eclipse).
2.  Add the following JARs to your generic `WEB-INF/lib` or build path:
       `mongo-java-driver-3.12.x.jar` (or newer)
        `gson-2.8.x.jar`
       `javax.servlet-api-4.0.x.jar`
3.  Build the project (Compile Java files).
4.  Deploy the project to Apache Tomcat.
    *   Context Path `/campus-lost-found`
    *   The API will be available at: `http://localhost:8080/campus-lost-found/api/...`

### 2. Frontend Setup
1.  Navigate to the `frontend` directory:
    ```bash
    cd frontend
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Start the development server:
    ```bash
    npm run dev
    ```
4.  Open `http://localhost:3000` (or the port shown) in your browser.


## 📚 Advanced Concepts "Viva" Logic

### Why MongoDB?
We chose MongoDB over SQL because item descriptions (Lost/Found) vary greatly. A strict schema might be too rigid. MongoDB allowed us to flexibility store attributes while indexing key fields like `category` and `date` for fast searches.

## Singleton Pattern in `MongoDBUtil`
To prevent the overhead of opening a new database connection for every HTTP request, we implemented the Singleton pattern. This ensures a single `MongoClient` instance is shared across the application, handling connection pooling automatically.

## DAO Pattern
We separated the database logic (`LostItemDAO`) from the business logic (Servlet). This makes the code **testable**, **maintainable**, and allows us to swap the database implementation (e.g., to SQL) without changing the Servlet code.


##  UI/UX Design
The interface follows a "Deep Indigo + Soft Cyan" theme (`#1a237e`, `#00bcd4`) to convey trust and calmness. We use a **Dashboard** layout for quick access to recent items and clear Call-to-Action buttons for reporting.


API Endpoints

| Method | Endpoint | Description |
|os|---|---|
| GET | `/api/lost-items` | Get all lost items |
| POST | `/api/lost-items` | Report a lost item |
| GET | `/api/found-items` | Get all found items |
| POST | `/api/found-items` | Report a found item |
| GET | `/api/search` | Search items by query/category |

👥 Team & Collaboration
This project is a group effort of 5 members.
Project Structure: See [STRUCTURE.md](./STRUCTURE.md) for folder details.
Team Roles: See [ROLES.md](./ROLES.md) for assigned responsibilities.
GitHub Workflow: Detailed steps for pushing code are in [ROLES.md#github-workflow--step-by-step](./ROLES.md#github-workflow--step-by-step).
New to GitHub?: Follow the [Beginner's Guide to Submission](./BEGINNER_GIT_GUIDE.md) for a step-by-step walkthrough.
