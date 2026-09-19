# Smart Alumni Network System – Design Document (DESIGN)

---

## 1. System Overview
The **Smart Alumni Network System** is an enterprise-grade academic platform connecting college students, alumni, and academic departments. The solution bridges the communication and engagement gap by providing a verified alumni repository, direct 1-on-1 mentorship scheduling, an internal job referral board, and a transparent departmental endowment funding system.

---

## 2. User Roles & Capabilities Matrix

| Feature / Capability | Student | Alumni | Admin |
|---|:---:|:---:|:---:|
| User Registration (Verification required for Alumni) | Yes | Yes (Verified) | Pre-seeded |
| Authentication & Session Management | Yes | Yes | Yes |
| View Role Dashboard & Live College Metrics | Yes | Yes | Yes |
| Search & Filter Alumni Directory | Yes | Yes | Yes |
| View Alumni Profiles & Mentorship Status | Yes | Yes | Yes |
| Schedule Mentorship Request (Topic, Date, Time) | **Yes** | No | No |
| Respond to Mentorship Requests (Accept / Reject) | No | **Yes** | View Only |
| View Scheduled Sessions & Feedback Notes | Yes | Yes | View Only |
| Post New Job Referral | No | **Yes** | View Only |
| Edit & Delete Own Job Referrals | No | **Yes** | No |
| Search & Apply for Job Opportunities | **Yes** | Yes | Yes |
| View Department Endowment Projects & Targets | Yes | Yes | Yes |
| Submit Financial Pledge to Endowment Project | Yes | **Yes** | Yes |
| Track Live Pledge Progress & Aggregated Funding | Yes | Yes | Yes |
| Receive In-App Alert Notifications | Yes | Yes | Yes |
| Overall System Audit & Metrics | No | No | **Yes** |

---

## 3. Module Design

### 3.1 Authentication & Verification Module
* **Alumni Verification Engine**: Interrogates the institutional `verified_graduates` table using register number, department, and graduation year before account activation.
* **Password Hashing Utility**: Salted SHA-256 hashing executed purely with `java.security.MessageDigest` and `java.security.SecureRandom`.
* **Session Handler**: Issues lightweight, secure session tokens tied to `userId`, `role`, and `name`, verified on every state-mutating HTTP endpoint.

### 3.2 Alumni Directory Module
* **Search & Filter Component**: Executes parameterized multi-condition SQL queries supporting:
  * Fuzzy name search (`full_name LIKE ?`)
  * Department filter (`dept_id = ?`)
  * Batch year filter (`batch_year = ?`)
  * Company search (`company LIKE ?`)
  * Designation search (`designation LIKE ?`)
  * Location filter (`location LIKE ?`)
  * Mentorship availability toggle (`is_mentor_available = 1`)
* **Presentation**: Card grid with avatar badges, career highlights, and one-click "Book Mentorship" modal triggers.

### 3.3 Mentorship Scheduling Module
* **Booking State Machine**:
  ```text
  [Student Submits Request]
             ↓
        (Pending)
       ↙         ↘
  (Accepted)   (Rejected)
       ↓
  (Completed)
  ```
* **Scheduling Attributes**: Session topic, custom message, preferred appointment date, preferred time slot, and mentor feedback/meeting details.

### 3.4 Job Referral Bulletin Board Module
* **Lifecycle**: Alumni create postings with job title, company, work mode/location, required skills, salary package range, job description, and external application URL.
* **Authorization Guard**: The backend verifies `job.alumni_id == current_user.alumni_id` before executing updates or deletes.
* **Student Discovery**: Students filter postings by keyword, technology, or company, and access direct application links.

### 3.5 Department Endowment & Pledge Tracker Module
* **Institutional Campaigns**: Academic projects with dedicated funding targets, current raised tallies, and descriptive charters (e.g. AI & Robotics Lab Fund, Student Scholarships).
* **Pledge Commitment**: Donors select a project, specify a monetary pledge amount, pledge date, and optional donor note.
* **Atomic Aggregation**: Upon pledge insertion, a transactional database trigger or service method updates `current_amount = current_amount + pledge_amount`, immediately reflecting across the visual progress bars.

---

## 4. Frontend Design & UI Navigation

### 4.1 Layout Structure
* **Header & Navigation Bar**:
  * College brand logo & title: *"Smart Alumni Network System — SIST"*.
  * Active user role badge (Student / Alumni / Admin) with one-click demo switcher.
  * Tab navigation items: Dashboard, Directory, Mentorship, Job Board, Endowment, Notifications.
  * User menu with profile modal trigger and Logout.
* **Hero Banner**: Highlighting college achievements, live alumni count, active referral jobs, and endowment totals.
* **Main Content Area**: Dynamic Single Page Application (SPA) container rendering views smoothly without full page reloads.
* **Toast Notification Center**: Fixed bottom-right toast stack for informative, success, warning, and error alerts.
* **Footer**: Institutional copyright, department accreditation note, and quick academic links.

### 4.2 UI Sitemap & Navigation Flow
```text
Home / Login / Register
  ├── Authentication Modals (Login, Register with Alumni Verification)
  └── Main Navigation Tabs:
        ├── 1. Dashboard (Role-tailored stats, recent activity cards)
        ├── 2. Alumni Directory (Filters, search bar, profile cards, book modal)
        ├── 3. Mentorship Hub (Browse mentors, student requests, alumni action queue)
        ├── 4. Job Referral Bulletin (Browse jobs, alumni "Post Job" modal, edit/delete)
        ├── 5. Department Endowment (Project cards, progress meters, pledge modal)
        ├── 6. Notifications Panel (System alerts, read/unread status)
        └── 7. User Profile Modal (View and update profile details)
```

---

## 5. Backend Design & Layered Architecture

The backend strictly observes a clean 4-tier separation:
1. **Controller Layer**: Inspects incoming `HttpExchange`, parses query parameters or JSON request body, enforces authentication/role permissions, and delegates to the Service.
2. **Service Layer**: Contains business logic, validation rules, state transitions, and authorization checks.
3. **Data Access Object (DAO) Layer**: Interacts with MySQL 8.0 via JDBC `PreparedStatement`, maps database `ResultSet` rows to domain Models.
4. **Model Layer**: Plain Old Java Objects (POJOs) representing domain entities with getters, setters, and serialization support.

---

## 6. Error Handling & Validation Strategy

* **Client-Side Validation**:
  * Required fields checked before `fetch()` invocation.
  * Regular expressions for institutional email (`^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$`), 10-digit Indian phone numbers (`^[6-9]\d{9}$`), and 4-digit years.
  * Graceful visual feedback with red field highlighting and non-blocking toast notifications.
* **Server-Side Validation**:
  * Defensive null and empty string checks.
  * SQL injection prevention via parameterized queries.
  * Consistent JSON error response schema:
    ```json
    {
      "success": false,
      "message": "Human-readable error description",
      "errorCode": "INVALID_CREDENTIALS"
    }
    ```
  * Appropriate HTTP status codes: `200 OK`, `201 Created`, `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`, `500 Internal Server Error`.

---

## 7. Security Considerations
1. **No Plaintext Passwords**: Every password is treated with SHA-256 and an isolated cryptographic salt before persistence.
2. **SQL Injection Immunity**: Zero string concatenation in database queries. Every dynamic value is bound using `PreparedStatement.setX()`.
3. **Cross-Site Scripting (XSS) Prevention**: Frontend utilizes `textContent` and safe DOM manipulation rather than raw `innerHTML` for user-generated strings.
4. **CORS & Safe Headers**: `Main.java` attaches standard headers (`Access-Control-Allow-Origin`, `Content-Type: application/json; charset=UTF-8`).
