# Smart Alumni Network System

An institution-grade, full-stack web application designed for engineering colleges to foster enduring connections between students, alumni, and academic departments.

**Institution:** Sathyabama Institute of Science and Technology (SIST)  
**Department:** Computer Science & Engineering (Artificial Intelligence)  
**Execution Platform:** Local Windows Execution (Double-click `run.bat`)  
**URL:** [http://localhost:8080](http://localhost:8080)

---

## 1. Project Overview

College alumni associations frequently struggle with fragmented alumni contact directories, unverified student claims, lack of structured mentorship channels, untracked referral opportunities, and opaque department endowment funding.

The **Smart Alumni Network System** solves these challenges by providing:
1. **Verified Alumni Registration**: Prevents impersonation by validating prospective alumni against official college graduation records (register number, batch, department).
2. **Searchable & Filterable Alumni Directory**: Enables students and administrators to discover seniors by company, role, batch, and mentorship availability.
3. **Structured Mentorship Scheduling**: Students browse available alumni mentors, book sessions with specific topics and date/time slots, while alumni accept/reject and provide feedback.
4. **Job Referral & Recruitment Bulletin Board**: Verified alumni publish, edit, and manage internal company job openings; students search and apply directly.
5. **Department Endowment & Pledge Tracker**: Showcases departmental infrastructure and scholarship campaigns with live target progress meters and alumni pledge recording.
6. **Activity Feed & Notifications**: Real-time in-app alerts for mentorship request status, new job postings, and pledge acknowledgments.

---

## 2. Strict Technology Stack (Zero Frameworks)

The project adheres strictly to the zero-framework academic constraint:

| Layer | Technology | Details |
|---|---|---|
| **Frontend** | HTML5 Semantic Markup | Clean academic layout, responsive CSS Grid / Flexbox |
| **Styling** | CSS3 (Vanilla) | Custom light academic theme, card elevations, animated modals, toast alerts |
| **Client Script** | Vanilla JavaScript ES6+ | Asynchronous `fetch()` API, SPA dynamic routing, zero jQuery or frontend frameworks |
| **Backend** | Java SE (Standard Edition) | Built-in `com.sun.net.httpserver.HttpServer` on port 8080 |
| **Data Access** | JDBC | Pure Java Database Connectivity with `PreparedStatement` |
| **Database** | MySQL 8.0 | Relational database normalized to Third Normal Form (3NF) |
| **Libraries** | MySQL Connector/J | Only `lib/mysql-connector-j.jar` (included in project) |

> **Strictly Disallowed & Excluded:** No React, Angular, Vue, Node.js, Express, Spring Boot, Python, Django, Flask, PHP, Bootstrap, Tailwind CSS, jQuery, TypeScript, MongoDB, or ORMs.

---

## 3. System Architecture

```text
                               Client Browser (User)
                                        │
                         (DOM Events & User Navigation)
                                        ▼
                  Frontend Layer (HTML5 / CSS3 / Vanilla JS)
                         frontend/index.html, css/, js/
                                        │
                        (Asynchronous HTTP JSON Requests)
                                        ▼
                   Java SE Web Server (Port 8080)
                 com.sun.net.httpserver.HttpServer (Main.java)
                                        │
                          (REST API Route Dispatcher)
                                        ▼
                                Controller Layer
                   src/controller/ (AuthController, AlumniController,
                     JobController, MentorshipController, EndowmentController)
                                        │
                            (Business Rules & Validation)
                                        ▼
                                 Service Layer
                   src/service/ (AuthService, AlumniService,
                     JobService, MentorshipService, EndowmentService)
                                        │
                            (JDBC Parameter Binding)
                                        ▼
                              Data Access Object (DAO)
                   src/dao/ (UserDAO, StudentDAO, AlumniDAO,
                     JobDAO, MentorshipDAO, EndowmentDAO)
                                        │
                         (PreparedStatement Queries)
                                        ▼
                            MySQL Server 8.0 (3NF)
                         smart_alumni relational database
```

---

## 4. Project Directory Structure

```text
Smart Alumni Network System/
│
├── frontend/                     # Modern academic web frontend
│   ├── index.html                # Semantic HTML5 SPA markup & modals
│   ├── css/
│   │   └── style.css             # Custom Vanilla CSS3 design system
│   └── js/
│       └── script.js             # Vanilla ES6+ SPA controller & fetch handlers
│
├── src/                          # Java SE source code
│   ├── Main.java                 # Entry point: HttpServer & Static file router
│   ├── controller/               # REST HTTP handlers
│   │   ├── AuthController.java
│   │   ├── AlumniController.java
│   │   ├── JobController.java
│   │   ├── MentorshipController.java
│   │   ├── EndowmentController.java
│   │   ├── NotificationController.java
│   │   ├── StatsController.java
│   │   └── HttpHelper.java
│   ├── service/                  # Business logic & validation
│   │   ├── AuthService.java
│   │   ├── AlumniService.java
│   │   ├── JobService.java
│   │   ├── MentorshipService.java
│   │   ├── EndowmentService.java
│   │   ├── NotificationService.java
│   │   └── StatsService.java
│   ├── dao/                      # JDBC Data Access Objects (PreparedStatement)
│   │   ├── UserDAO.java
│   │   ├── StudentDAO.java
│   │   ├── AlumniDAO.java
│   │   ├── JobDAO.java
│   │   ├── MentorshipDAO.java
│   │   ├── EndowmentDAO.java
│   │   ├── NotificationDAO.java
│   │   ├── DepartmentDAO.java
│   │   └── VerifiedGraduateDAO.java
│   ├── model/                    # Domain POJO entities
│   │   ├── User.java
│   │   ├── Student.java
│   │   ├── Alumni.java
│   │   ├── Job.java
│   │   ├── MentorshipRequest.java
│   │   ├── EndowmentProject.java
│   │   ├── Pledge.java
│   │   ├── Notification.java
│   │   ├── Department.java
│   │   └── VerifiedGraduate.java
│   └── util/                     # Pure Java SE utilities
│       ├── DBConnection.java     # JDBC connection factory & auto-schema init
│       ├── JsonUtil.java         # Zero-dependency JSON parser & serializer
│       └── PasswordUtil.java     # Salted SHA-256 cryptographic hashing
│
├── database/                     # MySQL 8.0 relational scripts
│   ├── schema.sql                # 3NF database schema with foreign keys
│   └── sample_data.sql           # Realistic seed records for evaluation
│
├── docs/                         # Comprehensive project documentation
│   ├── DEFINE.md                 # Scope, personas, 6 user stories, criteria
│   ├── DESIGN.md                 # System modules, auth flow, CRUD operations
│   ├── architecture.md           # DFD Level 0/1, ER specs, REST contracts
│   └── ACCEPTANCE_TESTING.md     # 18-point verification test matrix
│
├── lib/                          # Bundled libraries
│   └── mysql-connector-j.jar     # MySQL JDBC Driver JAR
│
├── db.properties                 # Easy database configuration file
├── README.md                     # Complete project runbook & documentation
└── run.bat                       # One-click Windows compilation & startup script
```

---

## 5. Quick Start on Windows (One-Click Execution)

### Prerequisites
1. **Java JDK** (Version 17, 21, or 26) installed and accessible in PATH (`javac -version`).
2. **MySQL Server 8.0** installed and running on port `3306`.

### Step 1: Configure Database Password (Optional)
If your local MySQL `root` user has a password, open `db.properties` in the project root:
```properties
db.host=localhost
db.port=3306
db.name=smart_alumni
db.user=root
db.password=your_mysql_password_here
```
*(If your MySQL root user has no password, leave `db.password=` empty).*

### Step 2: Initialize Database (If not already done)
Open MySQL Command Line Client or MySQL Workbench and run:
```sql
SOURCE database/schema.sql;
SOURCE database/sample_data.sql;
```
*(Note: `DBConnection.java` also automatically creates the database and populates tables upon first connection if MySQL is accessible!)*

### Step 3: Run the Application
Simply **double-click** `run.bat` in Windows Explorer (or execute it in Command Prompt / PowerShell):
```cmd
.\run.bat
```

What `run.bat` does automatically:
1. Validates that `javac` and `java` are installed.
2. Compiles all Java files in `src/` including `lib/mysql-connector-j.jar`.
3. Starts the backend `Main` server on `http://localhost:8080`.
4. Automatically opens your default web browser to the portal.

---

## 6. Pre-Seeded Test Credentials

Use these safe demo credentials to evaluate all features during project demonstration:

| Role | Email | Password | Persona Details |
|---|---|---|---|
| **System Administrator** | `admin@gmail.com` | `admin123` | Department Audit & Global Reporting |
| **Alumni (Mentor)** | `shiela@gmail.com` | `alumni123` | Shiela S. (TCS Senior AI Engineer, Batch 2024) |
| **Alumni (Mentor)** | `arun.kumar@gmail.com` | `alumni123` | Arun Kumar (Amazon AWS SDE-2, Batch 2023) |
| **Alumni (Mentor)** | `priya.s@gmail.com` | `alumni123` | Priya S. (Microsoft Product Manager, Batch 2022) |
| **Student** | `kiruthi@gmail.com` | `student123` | Kiruthika R. (3rd Year B.E. CSE AI) |
| **Student** | `imalini@gmail.com` | `student123` | Imalini Devi (4th Year B.E. CSE AI) |

> **Quick Switcher Bar:** At the top of the portal, use the quick-switch buttons to instantly log in as Student, Alumni, or Admin without typing!

---

## 7. Core Feature Walkthrough for Project Defense

### 1. Verified Alumni Registration (FR-01)
* **Viva Question:** *How does the system prevent unauthorized users from claiming to be alumni?*
* **Demonstration:**
  * Try registering as Alumni using an unverified register number (e.g., `99999999`). The system rejects it immediately.
  * Try registering using official graduate register number `44731006` (Karthik Subramanian, Batch 2024, CSE-AI). The system validates the graduate against the institutional verification vault (`verified_graduates`), completes registration, and marks the record as claimed.

### 2. Searchable Alumni Directory (FR-02)
* **Viva Question:** *How is multi-criteria filtering implemented without heavy frameworks?*
* **Demonstration:**
  * Navigate to the **Alumni Directory** tab.
  * Search by keyword `"TCS"`, filter by Department `"CSE-AI"`, or toggle `"Available Mentors Only"`.
  * The backend executes parameterized SQL with bound parameters, returning structured JSON for client-side card rendering.

### 3. Mentorship Scheduling Workflow (FR-03)
* **Viva Question:** *What is the lifecycle of a mentorship request?*
* **Demonstration:**
  * Log in as Student (`kiruthi@gmail.com`).
  * Find alumnus Shiela Soundararajan, click **Book Session**, select Topic *"Mock AI Technical Interview"*, Date, Time Slot, and submit.
  * Log in as Alumni (`shiela@gmail.com`), navigate to **Mentorship Hub**, review the pending request, click **Accept**, and enter meeting notes.
  * Log back in as Kiruthika; view the updated status badge and notification alert.

### 4. Job Referral Bulletin Board (FR-04)
* **Viva Question:** *How do you enforce role-based access control (RBAC) on job postings?*
* **Demonstration:**
  * As an Alumnus, click **+ Post New Job Referral** to publish an opening (Title, Company, Skills, Salary, Application Link).
  * As a Student, browse the bulletin board and click **Apply Now &rarr;**. Notice that post, edit, and delete buttons are hidden from students.
  * If an alumnus attempts to edit a job posted by another alumnus, the backend validates `job.alumni_id == current_user.alumni_id` and rejects unauthorized changes with HTTP 403.

### 5. Department Endowment & Pledge Tracker (FR-05)
* **Viva Question:** *How does the system manage financial pledges and ensure ACID compliance?*
* **Demonstration:**
  * Navigate to **Endowment Pledges**.
  * View active campaigns (e.g. *AI & Autonomous Robotics Lab Fund*, Goal: ₹1,500,000).
  * Click **Make a Pledge**, enter ₹50,000, and confirm.
  * In the backend, `EndowmentDAO.java` starts a database transaction (`conn.setAutoCommit(false)`), inserts into `pledges`, updates `endowment_projects.current_amount`, and commits atomically.
  * The progress bar and raised total update live on screen.

---

## 8. Technical Viva Defense Q&A Cheatsheet

1. **Why Java SE `HttpServer` instead of Spring Boot or Tomcat?**
   * *Answer:* Java SE provides `com.sun.net.httpserver.HttpServer` out of the box. It proves fundamental understanding of HTTP protocols, request-response handling, thread pooling (`Executors.newFixedThreadPool`), socket dispatching, and REST concepts without relying on high-level framework abstraction.

2. **How is JSON handled without external Jackson or Gson libraries?**
   * *Answer:* We built a clean, zero-dependency `JsonUtil.java` using recursive descent parsing and Java reflection. It converts Java Maps, Lists, and Beans to compliant JSON strings and parses incoming JSON payloads.

3. **How are passwords secured?**
   * *Answer:* Rather than storing plaintext passwords, `PasswordUtil.java` generates a 16-character cryptographic salt via `SecureRandom` and computes a salted SHA-256 hash using `MessageDigest`.

4. **How are SQL injection attacks prevented?**
   * *Answer:* Every database interaction across all 9 DAOs uses JDBC `PreparedStatement` with placeholder `?` bindings. No query strings are concatenated with user input.

5. **How does the Single Page Application (SPA) navigate without page reloads?**
   * *Answer:* `script.js` listens to tab clicks, dynamically toggles `.active` CSS classes on view containers, and asynchronously calls backend REST endpoints via `fetch()`, updating the DOM in-place.
