# Smart Alumni Network System – Technical Architecture & Specifications

---

## 1. Complete System Architecture

```text
               +-------------------------------------------------------------+
               |                  Client Web Browser (User)                  |
               +-------------------------------------------------------------+
                                              |
                                              | (User Interactions & Navigation)
                                              v
               +-------------------------------------------------------------+
               |         Frontend Presentation Layer (Zero Framework)        |
               |       • HTML5 Semantic Markup (index.html)                  |
               |       • CSS3 Responsive Design System (css/style.css)       |
               |       • Vanilla ES6+ JavaScript Controller (js/script.js)   |
               +-------------------------------------------------------------+
                                              |
                                              | (Asynchronous JSON HTTP Requests)
                                              v
               +-------------------------------------------------------------+
               |                 Java SE HTTP Web Server                     |
               |         com.sun.net.httpserver.HttpServer (Port 8080)       |
               |         • Static Asset File Server (MIME Dispatcher)        |
               |         • REST API Request Router & CORS Filter             |
               +-------------------------------------------------------------+
                                              |
                                              | (Dispatches to Controllers)
                                              v
               +-------------------------------------------------------------+
               |                      Controller Layer                       |
               |   • AuthController       • AlumniController                 |
               |   • MentorshipController • JobController                    |
               |   • EndowmentController  • NotificationController           |
               +-------------------------------------------------------------+
                                              |
                                              | (Invokes Business Logic)
                                              v
               +-------------------------------------------------------------+
               |                       Service Layer                         |
               |   • AuthService          • AlumniService                    |
               |   • MentorshipService    • JobService                       |
               |   • EndowmentService     • NotificationService              |
               |   (Validation, Authorization, Business Rules, Notifications)|
               +-------------------------------------------------------------+
                                              |
                                              | (Invokes Data Access Operations)
                                              v
               +-------------------------------------------------------------+
               |                  Data Access Object (DAO)                   |
               |   • UserDAO              • StudentDAO                       |
               |   • AlumniDAO            • MentorshipDAO                    |
               |   • JobDAO               • EndowmentDAO                     |
               |   (PreparedStatement, Parameter Binding, Entity Mapping)   |
               +-------------------------------------------------------------+
                                              |
                                              | (Standard JDBC Driver: com.mysql.cj.jdbc.Driver)
                                              v
               +-------------------------------------------------------------+
               |                    Relational Database                      |
               |                 MySQL Server 8.0 (3NF Schema)               |
               |  (users, verified_graduates, students, alumni, jobs, ...)   |
               +-------------------------------------------------------------+
```

---

## 2. Component Responsibilities

1. **Frontend (`frontend/`)**:
   * **`index.html`**: Provides semantic document structure, accessibility, modal skeletons, and container views for single-page responsiveness.
   * **`css/style.css`**: Provides academic visual styling, card layouts, CSS Grid / Flexbox, animated modals, button states, and toast notifications.
   * **`js/script.js`**: Drives client-side SPA routing, executes asynchronous `fetch()` API calls, enforces client validation, and dynamically renders DOM nodes.
2. **Java `HttpServer` (`Main.java`)**:
   * Listens on `http://localhost:8080`.
   * Serves static frontend assets (`.html`, `.css`, `.js`, images) with accurate MIME types.
   * Routes `/api/*` endpoints to dedicated Java controllers.
   * Manages request life-cycle, CORS headers, and uncaught exception handling.
3. **Controllers (`src/controller/`)**:
   * Extract URI parameters and JSON payloads.
   * Handle HTTP request verbs (GET, POST, PUT, DELETE).
   * Enforce session authentication headers and role authorization.
   * Serialize service outputs to JSON and transmit HTTP status codes.
4. **Services (`src/service/`)**:
   * Enforce business logic, state transitions (e.g. `Pending` -> `Accepted`), and data integrity.
   * Prevent duplicate registrations and unauthorized resource tampering.
   * Coordinate cross-entity workflows (e.g., creating a notification when a mentorship request is submitted).
5. **DAOs (`src/dao/`)**:
   * Execute parameterized SQL operations via `PreparedStatement`.
   * Prevent SQL injection vulnerabilities.
   * Map MySQL result sets (`ResultSet`) to Java Model objects.
6. **Models (`src/model/`)**:
   * Strongly-typed POJOs encapsulating domain state with getters, setters, and serialization helpers.
7. **Database (`MySQL 8.0`)**:
   * Enforces referential integrity, unique constraints, foreign keys, and ACID transactions.
8. **Utilities (`src/util/`)**:
   * **`DBConnection.java`**: Centralized JDBC connection factory supporting properties files and environment variables.
   * **`JsonUtil.java`**: Zero-dependency JSON parser and serializer written in pure Java SE.
   * **`PasswordUtil.java`**: Secure salted SHA-256 cryptographic password hashing.

---

## 3. REST-like HTTP API Contracts

| Method | Endpoint | Purpose | Request Body Format | Success Status | Auth Role Required |
|---|---|---|---|:---:|:---:|
| `POST` | `/api/auth/register` | Register Student or Alumni | JSON: `{ role, email, password, name, phone, deptId, regNo, batchYear, company, designation }` | `201 Created` | Public |
| `POST` | `/api/auth/login` | Authenticate user | JSON: `{ email, password, role }` | `200 OK` | Public |
| `GET` | `/api/auth/me` | Fetch active session details | None | `200 OK` | Any Authenticated |
| `POST` | `/api/auth/logout` | Invalidate current session | None | `200 OK` | Any Authenticated |
| `GET` | `/api/alumni/verify` | Verify graduate roll number | Query: `?regNo=...&batchYear=...&deptId=...` | `200 OK` | Public |
| `GET` | `/api/alumni` | Search & filter alumni | Query: `?search=...&deptId=...&batchYear=...&company=...&mentorOnly=...` | `200 OK` | Any Authenticated |
| `GET` | `/api/alumni/{id}` | Get alumni profile details | Path variable | `200 OK` | Any Authenticated |
| `PUT` | `/api/alumni/profile` | Update own alumni profile | JSON: `{ company, designation, location, phone, linkedinUrl, isMentorAvailable }` | `200 OK` | Alumni |
| `GET` | `/api/jobs` | Retrieve job postings | Query: `?search=...&location=...` | `200 OK` | Any Authenticated |
| `POST` | `/api/jobs` | Create new job posting | JSON: `{ jobTitle, company, location, jobType, salaryRange, description, requiredSkills, applicationLink }` | `201 Created` | Alumni |
| `PUT` | `/api/jobs/{id}` | Update existing job posting | JSON: `{ jobTitle, company, location, salaryRange, description, requiredSkills, applicationLink }` | `200 OK` | Author Alumni |
| `DELETE` | `/api/jobs/{id}` | Delete job posting | Path variable | `200 OK` | Author Alumni / Admin |
| `GET` | `/api/mentorship` | Get mentorship sessions | Query: `?role=student\|alumni` | `200 OK` | Student / Alumni |
| `POST` | `/api/mentorship` | Request mentorship session | JSON: `{ alumniId, topic, message, preferredDate, preferredTime }` | `201 Created` | Student |
| `PUT` | `/api/mentorship/{id}/status` | Update session status | JSON: `{ status: "Accepted"\|"Rejected"\|"Completed", responseNotes: "..." }` | `200 OK` | Alumni Mentor |
| `GET` | `/api/endowments` | List department campaigns | None | `200 OK` | Any Authenticated |
| `POST` | `/api/pledges` | Record financial pledge | JSON: `{ projectId, pledgeAmount, notes }` | `201 Created` | Alumni / Student / Admin |
| `GET` | `/api/pledges/my` | View user's pledges | None | `200 OK` | Any Authenticated |
| `GET` | `/api/notifications` | Fetch user alerts | None | `200 OK` | Any Authenticated |
| `PUT` | `/api/notifications/{id}/read` | Mark alert as read | Path variable | `200 OK` | Any Authenticated |
| `GET` | `/api/stats` | System audit & dashboard metrics | None | `200 OK` | Any Authenticated |

---

## 4. Data Flow Diagrams (DFD)

### 4.1 DFD Level 0 (Context Diagram)

```text
               +------------------------------------------------+
               |                Student / Alumnus / Admin       |
               +------------------------------------------------+
                        |                             ^
       (User Credentials, Requests,       (Directory Profiles, Session
       Job Postings, Pledges, Queries)   Updates, Jobs, Alerts, Stats)
                        |                             |
                        v                             |
               +------------------------------------------------+
               |                    [ 0.0 ]                     |
               |          Smart Alumni Network System           |
               +------------------------------------------------+
                        |                             ^
                (SQL Insert/Update/           (ResultSet Tuples,
                 Delete Statements)            Aggregated Totals)
                        |                             |
                        v                             |
               +------------------------------------------------+
               |              MySQL Database (3NF)              |
               +------------------------------------------------+
```

### 4.2 DFD Level 1 (Decomposition Diagram)

```text
User Input
   |
   +---> [1.0 Authentication & Verification] <---> (D1: users, D2: verified_graduates)
   |
   +---> [2.0 Alumni Directory Engine]       <---> (D3: alumni, D4: departments)
   |
   +---> [3.0 Mentorship Management]         <---> (D5: mentorship_sessions, D8: notifications)
   |
   +---> [4.0 Job Referral Bulletin Board]   <---> (D6: jobs)
   |
   +---> [5.0 Department Endowment Tracker]  <---> (D7: endowment_projects, D9: pledges)
   |
   +---> [6.0 Notification Service]          <---> (D8: notifications)
```

---

## 5. Entity-Relationship (ER) Specification

### 5.1 Entities and Attributes
1. **`departments`**:
   * `dept_id` (INT, PK, AUTO_INCREMENT)
   * `dept_code` (VARCHAR(20), UNIQUE, NOT NULL) — e.g., 'CSE-AI'
   * `dept_name` (VARCHAR(150), NOT NULL)
2. **`verified_graduates`** (Institutional Verification Vault):
   * `grad_id` (INT, PK, AUTO_INCREMENT)
   * `register_no` (VARCHAR(50), UNIQUE, NOT NULL)
   * `full_name` (VARCHAR(100), NOT NULL)
   * `dept_id` (INT, FK -> `departments.dept_id`, NOT NULL)
   * `batch_year` (INT, NOT NULL)
   * `degree` (VARCHAR(50), NOT NULL)
   * `is_registered` (BOOLEAN, DEFAULT FALSE)
3. **`users`**:
   * `user_id` (INT, PK, AUTO_INCREMENT)
   * `email` (VARCHAR(120), UNIQUE, NOT NULL)
   * `password_hash` (VARCHAR(128), NOT NULL)
   * `salt` (VARCHAR(64), NOT NULL)
   * `role` (ENUM('Student', 'Alumni', 'Admin'), NOT NULL)
   * `status` (ENUM('Active', 'Suspended'), DEFAULT 'Active')
   * `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
4. **`students`**:
   * `student_id` (INT, PK, AUTO_INCREMENT)
   * `user_id` (INT, FK -> `users.user_id`, UNIQUE, NOT NULL)
   * `register_no` (VARCHAR(50), UNIQUE, NOT NULL)
   * `full_name` (VARCHAR(100), NOT NULL)
   * `dept_id` (INT, FK -> `departments.dept_id`, NOT NULL)
   * `current_year` (INT, NOT NULL) — e.g., 3
   * `phone` (VARCHAR(15), NOT NULL)
   * `bio` (TEXT)
5. **`alumni`**:
   * `alumni_id` (INT, PK, AUTO_INCREMENT)
   * `user_id` (INT, FK -> `users.user_id`, UNIQUE, NOT NULL)
   * `register_no` (VARCHAR(50), UNIQUE, NOT NULL)
   * `full_name` (VARCHAR(100), NOT NULL)
   * `dept_id` (INT, FK -> `departments.dept_id`, NOT NULL)
   * `batch_year` (INT, NOT NULL)
   * `company` (VARCHAR(120), NOT NULL)
   * `designation` (VARCHAR(120), NOT NULL)
   * `location` (VARCHAR(100))
   * `phone` (VARCHAR(15), NOT NULL)
   * `linkedin_url` (VARCHAR(255))
   * `is_mentor_available` (BOOLEAN, DEFAULT TRUE)
6. **`mentorship_sessions`**:
   * `session_id` (INT, PK, AUTO_INCREMENT)
   * `student_id` (INT, FK -> `students.student_id`, NOT NULL)
   * `alumni_id` (INT, FK -> `alumni.alumni_id`, NOT NULL)
   * `topic` (VARCHAR(150), NOT NULL)
   * `message` (TEXT, NOT NULL)
   * `preferred_date` (DATE, NOT NULL)
   * `preferred_time` (VARCHAR(50), NOT NULL)
   * `status` (ENUM('Pending', 'Accepted', 'Rejected', 'Completed'), DEFAULT 'Pending')
   * `response_notes` (TEXT)
   * `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
7. **`jobs`**:
   * `job_id` (INT, PK, AUTO_INCREMENT)
   * `alumni_id` (INT, FK -> `alumni.alumni_id`, NOT NULL)
   * `job_title` (VARCHAR(150), NOT NULL)
   * `company` (VARCHAR(120), NOT NULL)
   * `location` (VARCHAR(100), NOT NULL)
   * `job_type` (VARCHAR(50), DEFAULT 'Full-Time')
   * `salary_range` (VARCHAR(80))
   * `description` (TEXT, NOT NULL)
   * `required_skills` (VARCHAR(255), NOT NULL)
   * `application_link` (VARCHAR(255), NOT NULL)
   * `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
8. **`endowment_projects`**:
   * `project_id` (INT, PK, AUTO_INCREMENT)
   * `dept_id` (INT, FK -> `departments.dept_id`, NOT NULL)
   * `title` (VARCHAR(180), NOT NULL)
   * `description` (TEXT, NOT NULL)
   * `target_amount` (DECIMAL(12,2), NOT NULL)
   * `current_amount` (DECIMAL(12,2), DEFAULT 0.00)
   * `status` (ENUM('Active', 'Funded', 'Completed'), DEFAULT 'Active')
   * `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
9. **`pledges`**:
   * `pledge_id` (INT, PK, AUTO_INCREMENT)
   * `project_id` (INT, FK -> `endowment_projects.project_id`, NOT NULL)
   * `user_id` (INT, FK -> `users.user_id`, NOT NULL)
   * `pledge_amount` (DECIMAL(10,2), NOT NULL)
   * `pledge_date` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
   * `payment_status` (ENUM('Pledged', 'Fulfilled', 'Cancelled'), DEFAULT 'Pledged')
   * `notes` (VARCHAR(255))
10. **`notifications`**:
    * `notification_id` (INT, PK, AUTO_INCREMENT)
    * `user_id` (INT, FK -> `users.user_id`, NOT NULL)
    * `title` (VARCHAR(150), NOT NULL)
    * `message` (TEXT, NOT NULL)
    * `is_read` (BOOLEAN, DEFAULT FALSE)
    * `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)

### 5.2 Cardinalities and Relationships
* `departments` (1) ──── (N) `students`
* `departments` (1) ──── (N) `alumni`
* `departments` (1) ──── (N) `verified_graduates`
* `departments` (1) ──── (N) `endowment_projects`
* `users` (1) ──── (1) `students` (Exclusive Arc)
* `users` (1) ──── (1) `alumni` (Exclusive Arc)
* `users` (1) ──── (N) `pledges`
* `users` (1) ──── (N) `notifications`
* `alumni` (1) ──── (N) `jobs` (1 Alumnus posts Many Jobs)
* `students` (1) ──── (N) `mentorship_sessions` (1 Student books Many Sessions)
* `alumni` (1) ──── (N) `mentorship_sessions` (1 Alumnus mentors Many Sessions)
* `endowment_projects` (1) ──── (N) `pledges` (1 Project accumulates Many Pledges)
