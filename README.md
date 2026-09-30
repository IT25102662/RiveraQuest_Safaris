# Web-Based Boat Safari Trip Management System
**SE2030 - Software Engineering | SLIIT Year 2 Semester 1**

A full-stack, enterprise-ready web application for boat safari trip reservations, fleet management, safety monitoring, marketing promotions, and operational reporting. The database schema is aligned with the team's IT2140 Database Design & Development report — a normalized, report-accurate relational model backing every feature.

---

## 🌟 Key Features & Core Modules

1. **Customer & Staff Management**: Separate `Customer` and `Staff` account types, with `Staff` split into five roles via a JPA joined-inheritance hierarchy (`BookingOfficer`, `FleetManager`, `SafetyOfficer`, `MarketingOfficer`, `Administrator`)
2. **Online Trip Booking Management**: Browse scheduled trips by route, interactive seat map selector grid, promo code voucher application, real-time overbooking and double-booking prevention, digital boarding pass receipts.
3. **Boat, Route & Guide Management**: Vessel profiles, seaworthiness status, fuel & engine diagnostics, maintenance logs, plus dedicated `Route` (origin/destination) and `Guide` entities that each `Trip` references.
4. **Safety & Emergency Management**: Pre-departure audit checklist (life jackets count, first aid kits, emergency contact numbers), weather advisory status, departure clearance approval.
5. **Marketing Management**: Trip scheduling and pricing, discount voucher codes, tourist feedback reviews and star ratings.
6. **Trip Schedule & Operational Reports**: Departure timetables, boat & guide assignments, system summary analytics, revenue reports (gross, discount, net), boat utilization.
7. **Audit Logging & Crew Tracking**: `AuditLog` and `Crew` tables per the DB report, supporting future extension into full crew assignment tracking.

---

## 🛠 Tech Stack

- **Frontend**: HTML5, Tailwind CSS, JavaScript (Vanilla ES6+ SPA Architecture)
- **Backend**: Spring Boot 3.2, Java 21
- **Persistence**: Spring Data JPA + Hibernate, running on **Microsoft SQL Server** (`BoatSafariDB`)
- **API**: RESTful Web Services with JSON & CORS support

---

## 👥 User Roles & Demo Accounts

| Role | Demo User Email | Default Password | Capabilities |
| :--- | :--- | :--- | :--- |
| **Customer / Tourist** | `tourist@gmail.com` | `tourist123` | Browse trips, select seats, book trips, view history, write reviews |
| **Booking / Desk Officer** | `desk@boatsafari.lk` | `desk123` | Manage customer bookings, walk-in counter reservations, validation |
| **Fleet / Boat Manager** | `fleet@boatsafari.lk` | `fleet123` | Add/update boats, capacity limits, engine status, maintenance logs |
| **Safety & Operations Officer**| `safety@boatsafari.lk` | `safety123` | Pre-departure safety checklist audit, grant departure clearance |
| **Marketing Officer** | `marketing@boatsafari.lk` | `marketing123` | Manage trips, promo voucher codes, view customer ratings |
| **System Administrator** | `admin@boatsafari.lk` | `admin123` | Full system oversight, user management, revenue & operational reports |

---

## 🚀 How to Run the Application Locally

### Prerequisites
- Java JDK 21
- Apache Maven (or run via IDE)
- **Microsoft SQL Server** with a database named `BoatSafariDB` created, and SQL Server Authentication enabled (username/password login, not just Windows Authentication)

### Step 1: Configure the database connection
Open `src/main/resources/application.properties` and confirm it points at your SQL Server instance:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=BoatSafariDB;encrypt=true;trustServerCertificate=true;
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.username=sa
spring.datasource.password=YOUR_SQL_SERVER_PASSWORD
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
```

### Step 2: Build & Start the Backend Application
```bash
mvn spring-boot:run
```
*(Or in IntelliJ IDEA: Right-click `BoatSafariApplication.java` → Run)*

Spring Data JPA + Hibernate will automatically create and populate every table in `BoatSafariDB` on first startup via `DataInitializer`.

### Step 3: Access the Web Application

http://localhost:8080

---

## 🌐 REST API Endpoints Summary

- **Authentication**: `POST /api/auth/login`, `POST /api/auth/register`
- **Users** *(legacy, Admin's User & Roles page only)*: `GET /api/users`, `PUT /api/users/{id}`, `DELETE /api/users/{id}`
- **Boats**: `GET /api/boats`, `POST /api/boats`, `PUT /api/boats/{id}`, `DELETE /api/boats/{id}`
- **Maintenance**: `POST /api/boats/{id}/maintenance`, `GET /api/boats/{id}/maintenance`, `GET /api/boats/maintenance/all`
- **Routes**: `GET /api/routes`
- **Guides**: `GET /api/guides`
- **Trips**: `GET /api/trips`, `GET /api/trips/{id}`, `POST /api/trips`, `PUT /api/trips/{id}`, `PUT /api/trips/{id}/status`, `DELETE /api/trips/{id}`
- **Bookings**: `GET /api/bookings`, `GET /api/bookings/{id}`, `POST /api/bookings`, `PUT /api/bookings/{id}/cancel`, `PUT /api/bookings/{id}/validate`
- **Safety**: `GET /api/safety/trip/{tripId}`, `POST /api/safety/trip/{tripId}`, `PUT /api/safety/trip/{tripId}/approve`
- **Promotions**: `GET /api/promotions`, `GET /api/promotions/search`, `POST /api/promotions`, `PUT /api/promotions/{id}`, `DELETE /api/promotions/{id}`
- **Reviews**: `GET /api/reviews`, `GET /api/reviews/search`, `GET /api/reviews/summary`, `POST /api/reviews`, `PUT /api/reviews/{id}/status`, `DELETE /api/reviews/{id}`
- **Reports**: `GET /api/reports/summary`, `GET /api/reports/revenue`, `GET /api/reports/boats`

---
