# Smart Alumni Network System – Project Definition Document (DEFINE)

---

## 1. Project Scope

### 1.1 In Scope
The **Smart Alumni Network System** is an interactive, academic community portal engineered for engineering colleges to foster enduring connections between students, alumni, and academic departments. The following functional modules are fully in-scope:

* **Role-Based Authentication & Session Management**: Secure registration and login for Students, Alumni, and System Administrators with role-tailored dashboards and SHA-256 password hashing.
* **Verified Alumni Registration (FR-01)**: Verification of prospective alumni against college graduation records (register/roll number, department, graduation batch, degree) before granting alumni privileges.
* **Filterable & Searchable Alumni Directory (FR-02)**: Search and filter across batch year, department, current company, professional designation, location, and mentorship availability with detailed profile view.
* **Structured Mentorship Scheduling (FR-03)**: Student browsing of available alumni mentors, session booking with specific topic, preferred date and time, and request lifecycle management (Pending, Accepted, Rejected, Completed) with mentor feedback notes.
* **Job Referral & Opportunity Bulletin Board (FR-04)**: Complete CRUD operations for alumni to publish job opportunities (title, company, skills, compensation, location, external link), with real-time student searching and application routing.
* **Department Endowment & Funding Pledge Tracking (FR-05)**: Tracking of departmental infrastructure, research, and scholarship endowment projects; pledging mechanism for alumni with real-time aggregation of funds raised against target goals.
* **Notifications & Activity Feed**: System notifications for mentorship requests, status updates, new job postings, and pledge acknowledgments.
* **Local Windows Execution & Viva Readiness**: Zero-external-framework architecture (HTML5, CSS3, Vanilla ES6+ JS, Java SE `HttpServer`, JDBC, MySQL 8.0) runnable locally with a single-click script (`run.bat`).

### 1.2 Out of Scope
To maintain academic focus and comply with the zero-external-framework constraint, the following elements are intentionally excluded:
* Integration with commercial third-party payment gateways (Razorpay, Stripe) for monetary transactions (pledges are tracked as formal donor commitments).
* Real-time peer-to-peer WebRTC video conferencing (mentorship sessions are scheduled with venue/meeting link details).
* Native mobile applications (iOS/Android) — the responsive web frontend supports mobile viewports via modern CSS Flexbox and Grid.
* External third-party OAuth2 identity providers (Google/GitHub sign-in) in favor of self-contained, institution-verifiable credentials.

---

## 2. Stakeholder Personas

### Persona 1: The Engineering Student
* **Name**: Kiruthika Ramanathan
* **Role**: 3rd Year B.E. Computer Science and Engineering (Artificial Intelligence), SIST
* **Background**: Seeking career guidance, industry referrals, and technical mentorship in AI/ML engineering as campus placements approach.
* **Needs & Goals**:
  * Easily find alumni working in top tech firms (e.g., TCS, Amazon, Microsoft).
  * Book 1-on-1 mentorship sessions for resume reviews and interview preparation.
  * Browse job openings and internship referrals posted exclusively by verified alumni.
* **Frustrations**: Unresponsive LinkedIn InMails, outdated batch contact spreadsheets, lack of formal mentorship scheduling.

### Persona 2: The Established Alumnus
* **Name**: Shiela Soundararajan
* **Role**: Senior AI Engineer at TCS (Batch of 2024, CSE AI Alumna)
* **Background**: Active alumnus passionate about giving back to her alma mater through career mentorship, recruiting junior talent, and contributing to department lab infrastructure.
* **Needs & Goals**:
  * A verified alumni identity reflecting her graduation credentials.
  * An intuitive board to post internal referral opportunities directly to qualified students.
  * Controlled mentorship request workflows with date/time options and accept/reject controls.
  * A transparent method to pledge contributions to departmental endowment campaigns (e.g., AI & Robotics Lab Fund).
* **Frustrations**: Unorganized email inquiries from students, no centralized place to track institutional development projects.

### Persona 3: The Department Administrator / Head of Department
* **Name**: Dr. K. Narayanan
* **Role**: Alumni Relations Coordinator & Professor, Department of CSE AI
* **Background**: Responsible for maintaining accurate alumni records, auditing mentorship connections, and tracking departmental endowment pledges for accreditation reports (NAAC/NBA).
* **Needs & Goals**:
  * Comprehensive administrative view of total registered students, verified alumni, active jobs, and scheduled mentorships.
  * Management of department endowment campaigns and aggregated pledge tracking.
  * Ensuring unverified individuals cannot pose as alumni.
* **Frustrations**: Fragmented records across spreadsheets, difficulty aggregating alumni employment and giving metrics.

---

## 3. User Stories

1. **US-01 (Alumni Verification)**:
   > *As an alumnus, I want to register using my college register number and graduation year, so that my account is verified against official institutional records and grants me alumni privileges.*
2. **US-02 (Alumni Directory Search)**:
   > *As a student, I want to search and filter alumni by company, department, and batch year, so that I can discover relevant seniors working in my target domain.*
3. **US-03 (Mentorship Request & Scheduling)**:
   > *As a student, I want to schedule a mentorship session with a chosen alumnus by submitting a topic and preferred date/time, so that I can receive structured career guidance.*
4. **US-04 (Mentorship Lifecycle Management)**:
   > *As an alumnus mentor, I want to review incoming student mentorship requests and accept or reject them with response notes, so that I can manage my availability effectively.*
5. **US-05 (Job Referral Publishing)**:
   > *As a verified alumnus, I want to post, edit, and manage job openings and referral links, so that students from my college can apply for openings in my company.*
6. **US-06 (Endowment Campaign Pledging)**:
   > *As an alumnus or donor, I want to view department endowment projects and submit monetary pledges, so that I can financially support my department's facilities and student scholarships.*

---

## 4. Acceptance Criteria (Given / When / Then)

### AC-01: Verified Alumni Registration
* **Given** an official graduate record exists in the college verification repository (`verified_graduates`),
* **When** an alumnus provides their matching register number, graduation batch, and department during registration,
* **Then** the system registers the user with role `Alumni`, marks their profile as verified, and unlocks job posting and mentorship features.
* **Given** an invalid or non-matching register number is entered,
* **When** the alumnus attempts to complete registration,
* **Then** the system rejects registration with an explicit validation error: *"Register number not found in college graduation records."*

### AC-02: Alumni Directory Search & Filter
* **Given** multiple verified alumni are registered across diverse companies and departments,
* **When** a user types a search term (e.g. "TCS") or selects a department filter,
* **Then** the directory updates immediately to display only matching alumni profile cards containing name, company, designation, batch, and mentorship status.

### AC-03: Student Mentorship Session Booking
* **Given** an authenticated student is viewing an alumnus who is marked as available for mentorship,
* **When** the student selects a session topic, preferred date, time slot, and message, and submits the form,
* **Then** the system creates a mentorship session in `Pending` status and notifies the alumnus mentor.

### AC-04: Alumni Mentorship Request Response
* **Given** an alumnus mentor has pending mentorship requests,
* **When** the alumnus reviews a request and clicks "Accept" or "Reject" along with optional feedback notes,
* **Then** the system updates the session status in MySQL, generates a notification for the student, and displays the scheduled session in the student's dashboard.

### AC-05: Job Referral CRUD Operations
* **Given** an authenticated alumnus is logged in,
* **When** the alumnus submits a new job posting with title, company, location, skills, and application link,
* **Then** the job becomes visible to all students on the job board, and the alumnus retains permissions to update or delete the posting.
* **Given** an authenticated student is logged in,
* **When** the student accesses the job bulletin board,
* **Then** they can view and apply for jobs, but cannot see create, edit, or delete controls.

### AC-06: Department Endowment Pledging & Progress Tracking
* **Given** an active endowment campaign with a funding target (e.g., Target: ₹1,000,000, Raised: ₹450,000),
* **When** an alumnus pledges ₹50,000 towards the project,
* **Then** the system records the pledge, atomically updates the campaign's `current_amount` to ₹500,000, updates the visual progress bar to 50%, and creates a donor confirmation record.

---

## 5. Functional Requirements (FR)

| ID | Module | Description | Priority |
|---|---|---|---|
| **FR-01** | Verification | Verify graduation roll number, department, and batch year against college records prior to granting alumni status. | High |
| **FR-02** | Directory | Provide interactive, multi-criteria search and filtering for alumni directory. | High |
| **FR-03** | Mentorship | Allow students to request sessions with topic, date, and time; enable alumni to accept/reject and manage schedules. | High |
| **FR-04** | Job Bulletin | Enable alumni to publish, update, and delete job openings; allow students to filter and apply. | High |
| **FR-05** | Endowment | Track department endowment projects, pledge commitments, and aggregate funding progress. | High |
| **FR-06** | Authentication | Provide role-based access control (Student, Alumni, Admin) with secure password hashing. | High |
| **FR-07** | Dashboard | Display personalized statistical cards and recent activities per role. | Medium |
| **FR-08** | Notifications | Deliver in-app alert notifications for mentorship requests, status changes, and pledges. | Medium |
| **FR-09** | Admin Audit | Enable department administrators to monitor total users, active jobs, mentorships, and pledge tallies. | Medium |

---

## 6. Non-Functional Requirements (NFR)

* **NFR-01 (Performance)**: Static web assets and JSON API responses must be served with sub-100ms latency on localhost under standard college evaluation workloads.
* **NFR-02 (Security)**:
  * Passwords hashed using SHA-256 with unique per-user cryptographic salts.
  * All database operations executed exclusively via parameterized `PreparedStatement` to eliminate SQL injection vulnerabilities.
  * Role authorization enforced on all API endpoints.
* **NFR-03 (Usability)**: Clean, academic-themed user interface adhering to modern visual hierarchy, readable typography, intuitive tab switching, and instant toast notifications.
* **NFR-04 (Reliability & Integrity)**: Database schema strictly adheres to Third Normal Form (3NF) with foreign key constraints, default timestamps, and transactional pledge updates.
* **NFR-05 (Maintainability & Clean Architecture)**: Modular separation of concerns into Model, DAO, Service, and Controller layers in pure Java SE, easily explainable during a student viva defense.
* **NFR-06 (Compatibility)**: Fully compatible with Windows 10/11, Java SE 17 through 26, MySQL 8.0, and any modern web browser (Edge, Chrome, Firefox).
* **NFR-07 (Zero Framework Dependency)**: Built strictly with HTML5, CSS3, Vanilla ES6+ JavaScript, Java SE `HttpServer`, and standard JDBC.

---

## 7. Business Rules

1. **Verification Gate**: Only users whose register number matches an institutional record in `verified_graduates` can complete registration as an Alumnus.
2. **Role Boundaries**:
   * Students cannot create, edit, or delete job postings.
   * Alumni cannot schedule mentorship sessions with other alumni as a student.
   * Administrators hold global read-only audit permissions across all metrics, plus department campaign administration.
3. **Session Integrity**: Mentorship sessions cannot be booked for past dates or times.
4. **Ownership Verification**: Alumni may only modify or delete job referrals authored by their own user ID.
5. **Funding Transparency**: All alumni pledges must be linked to a valid `project_id` and must be greater than zero.
6. **Input Validation**: Client and server must rigorously validate email formats (`^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$`), 10-digit telephone numbers, and required string lengths.
