This project is a Java-based web application that demonstrates how to build a backend system using Java Servlets, JDBC, and PostgreSQL hosted online via Neon.
The application supports database connectivity, data persistence, and structured backend logic following good software engineering practices.

📌 Project Overview

The purpose of this project is to:

Build a Java backend application

Connect Java to an online PostgreSQL database (Neon)

Perform database operations using JDBC

Demonstrate clean project structure and configuration

Follow best practices for Java web applications

This project is suitable for academic submission, learning backend development, and real-world deployment practice.

🧰 Technologies Used
Technology	Description
Java	Core programming language
Java Servlets	Handles HTTP requests and responses
JDBC	Database connectivity
PostgreSQL	Relational database
Neon	Serverless cloud PostgreSQL provider
Maven / Gradle	Dependency management
Apache Tomcat	Servlet container
Git & GitHub	Version control


🗄️ Database (Neon PostgreSQL)

This project uses Neon, a serverless PostgreSQL platform, meaning:

No local database installation required

Secure cloud-hosted PostgreSQL

SSL-enabled connections

Always available online

🔗 Database Connection Example

Neon provides a JDBC connection string like:

jdbc:postgresql://<host>.neon.tech/<database>?sslmode=require

⚙️ Environment Configuration
Option 1: Using Environment Variables (Recommended)

Set the following variables:

PGHOST=<your-neon-host>.neon.tech
PGDATABASE=<database-name>
PGUSER=<username>
PGPASSWORD=<password>
PGPORT=5432

Option 2: Using a .env or properties file
DATABASE_URL=jdbc:postgresql://<user>:<password>@<host>.neon.tech/<dbname>?sslmode=require


⚠️ Do not commit credentials to GitHub.

📦 Dependencies
Maven (pom.xml)
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <version>42.6.0</version>
</dependency>

Gradle
implementation 'org.postgresql:postgresql:42.6.0'

🚀 How to Run the Project
1️⃣ Clone the Repository
git clone https://github.com/Nboss21/java-project.git
cd java-project

2️⃣ Configure Database Credentials

Set Neon PostgreSQL credentials using environment variables or config files.

3️⃣ Build the Project
mvn clean install


or

gradle build

4️⃣ Deploy to Tomcat

Copy the generated .war file to the webapps folder of Apache Tomcat

Start the Tomcat server

5️⃣ Access the Application
http://localhost:8080/

🧪 Application Features

Establishes secure connection to Neon PostgreSQL

Executes SQL queries using JDBC

Stores and retrieves data from the database

Handles HTTP requests via Java Servlets

Uses structured backend logic
