# Smart Alumni Network System – Acceptance Testing Document

This document records the verification matrix and acceptance criteria validation for all core functional and non-functional requirements of the **Smart Alumni Network System**.

---

## 1. Acceptance Testing Summary

| Total Test Cases | Passed | Failed | Status |
|:---:|:---:|:---:|:---:|
| 18 | 18 | 0 | **100% PASSED** |

---

## 2. Test Execution Matrix

### Test Case TC-01: Verified Alumni Registration (FR-01)
* **Requirement**: FR-01 – Verified Alumni Registration
* **Preconditions**: Graduate record exists in institutional database (`register_no`: '44731006', Batch: 2024, Dept: CSE AI).
* **Test Steps**:
  1. Open registration modal at `http://localhost:8080`.
  2. Select role as "Alumni".
  3. Enter Name: "Karthik Subramanian", Register No: "44731006", Dept: CSE-AI, Batch: 2024, Company: "Infosys", Designation: "Systems Engineer".
  4. Submit form.
* **Expected Result**: System verifies register number against `verified_graduates`, successfully inserts record into `users` and `alumni`, marks record as registered, and displays success toast.
* **Actual Result**: User registered successfully with role `Alumni`.
* **Status**: **PASS**

### Test Case TC-02: Alumni Registration Rejection on Non-Matching Register Number (FR-01)
* **Requirement**: FR-01 – Verified Alumni Registration
* **Preconditions**: Register number '99999999' does not exist in `verified_graduates`.
* **Test Steps**:
  1. Open registration modal, select "Alumni".
  2. Enter Register No: "99999999", Batch: 2024, Dept: CSE-AI.
  3. Submit registration.
* **Expected Result**: Backend validation halts registration with HTTP 400 and message: *"Verification Failed: Register number 99999999 was not found in college graduation records."*
* **Actual Result**: Verification failed message displayed, registration blocked.
* **Status**: **PASS**

### Test Case TC-03: Student Registration
* **Requirement**: FR-06 – Authentication & User Management
* **Preconditions**: Email `newstudent@gmail.com` is not in database.
* **Test Steps**:
  1. Open registration modal, select "Student".
  2. Enter Name: "Sneha Nair", Email: "newstudent@gmail.com", Pass: "student123", Reg No: "44732010", Phone: "9876543210", Dept: CSE-AI, Year: 3.
  3. Submit registration.
* **Expected Result**: Student created with role `Student`, welcome notification generated, password hashed with SHA-256 and unique salt.
* **Actual Result**: User created, notification stored in MySQL, status 201 Created.
* **Status**: **PASS**

### Test Case TC-04: User Authentication & Role Detection (FR-06)
* **Requirement**: FR-06 – Authentication
* **Preconditions**: User `shiela@gmail.com` exists as Alumni.
* **Test Steps**:
  1. Enter Email: "shiela@gmail.com", Password: "alumni123", Role: "Alumni".
  2. Click "Sign In".
* **Expected Result**: HTTP 200 returned with user profile payload; navbar updates to show "Shiela Soundararajan", role badge "Alumni", and unlocks "+ Post a Job Referral" button.
* **Actual Result**: Login successful, alumni role controls unlocked.
* **Status**: **PASS**

### Test Case TC-05: Invalid Password Rejection
* **Requirement**: FR-06 – Authentication
* **Preconditions**: User `admin@gmail.com` exists.
* **Test Steps**:
  1. Enter Email: "admin@gmail.com", Password: "wrongpassword", Role: "Admin".
  2. Click "Sign In".
* **Expected Result**: HTTP 401 returned with *"Invalid email or password."* Toast error displayed.
* **Actual Result**: Rejected with friendly toast notification.
* **Status**: **PASS**

### Test Case TC-06: Alumni Directory Search & Multi-Filter (FR-02)
* **Requirement**: FR-02 – Alumni Directory
* **Preconditions**: Pre-seeded alumni in database across TCS, Amazon, Microsoft, etc.
* **Test Steps**:
  1. Navigate to "Alumni Directory" tab.
  2. Type "TCS" in search field.
  3. Select Department filter "CSE-AI".
* **Expected Result**: Directory dynamically filters cards to show Shiela Soundararajan (TCS, Senior AI Engineer, Batch 2024).
* **Actual Result**: Exactly matching alumni card displayed.
* **Status**: **PASS**

### Test Case TC-07: Mentorship Availability Filter (FR-02)
* **Requirement**: FR-02 – Alumni Directory
* **Preconditions**: Rajesh Venkat has `is_mentor_available = FALSE`.
* **Test Steps**:
  1. Navigate to "Alumni Directory".
  2. Check "Available Mentors Only" checkbox.
* **Expected Result**: Only alumni with active mentorship availability are rendered; unavailable alumni are filtered out.
* **Actual Result**: Filter updates grid immediately.
* **Status**: **PASS**

### Test Case TC-08: Student Mentorship Request Submission (FR-03)
* **Requirement**: FR-03 – Mentorship Scheduling
* **Preconditions**: Student `kiruthi@gmail.com` logged in.
* **Test Steps**:
  1. View alumnus Shiela Soundararajan in directory.
  2. Click "Book Session".
  3. Enter Topic: "Deep Learning Interview Mock", Date: "2026-09-30", Time: "06:00 PM - 06:45 PM", Message: "Seeking feedback on CNN project."
  4. Click "Submit Mentorship Request".
* **Expected Result**: Session saved in `mentorship_sessions` in `Pending` status; notification generated for Shiela; request appears on student's session table.
* **Actual Result**: Request created, HTTP 201 Created returned.
* **Status**: **PASS**

### Test Case TC-09: Alumni Mentorship Acceptance Workflow (FR-03)
* **Requirement**: FR-03 – Mentorship Scheduling
* **Preconditions**: Alumni `shiela@gmail.com` logged in with pending request from Kiruthika.
* **Test Steps**:
  1. Open "Mentorship Hub" tab.
  2. Locate request from Kiruthika Ramanathan.
  3. Click "Accept", enter meeting note: "Confirmed! Link: meet.google.com/abc-defg-hij".
* **Expected Result**: Session status changes to `Accepted`, response notes persisted in MySQL, notification sent to student.
* **Actual Result**: Status updated to `Accepted`, notification created in MySQL.
* **Status**: **PASS**

### Test Case TC-10: Alumni Mentorship Rejection Workflow (FR-03)
* **Requirement**: FR-03 – Mentorship Scheduling
* **Preconditions**: Alumni logged in with pending session request.
* **Test Steps**:
  1. Click "Reject", enter reason: "Currently on travel."
* **Expected Result**: Status changes to `Rejected`, notes saved, student notified.
* **Actual Result**: Status updated to `Rejected`.
* **Status**: **PASS**

### Test Case TC-11: Alumni Job Referral Posting (FR-04)
* **Requirement**: FR-04 – Job Referral Bulletin Board
* **Preconditions**: Alumnus logged in.
* **Test Steps**:
  1. Open "Job Bulletin" tab.
  2. Click "+ Post New Job Referral".
  3. Enter Title: "AI Research Assistant", Company: "TCS Research", Location: "Chennai", Salary: "₹8 LPA", Skills: "Python, PyTorch", Link: "https://tcs.com/apply", Desc: "Undergraduate research referral."
  4. Submit form.
* **Expected Result**: Job created in `jobs` table with author `alumni_id`, visible on public bulletin board.
* **Actual Result**: Job created successfully, rendered in list.
* **Status**: **PASS**

### Test Case TC-12: Student Access Restriction on Job Posting (FR-04)
* **Requirement**: FR-04 – Job Referral Bulletin Board
* **Preconditions**: Student logged in.
* **Test Steps**:
  1. Open "Job Bulletin" tab.
  2. Attempt to trigger job posting.
* **Expected Result**: "+ Post New Job Referral" button is completely hidden from students; backend returns HTTP 403 Forbidden if attempted directly.
* **Actual Result**: UI button hidden, backend enforces authorization.
* **Status**: **PASS**

### Test Case TC-13: Job Referral Update (FR-04)
* **Requirement**: FR-04 – Job Referral Bulletin Board
* **Preconditions**: Alumnus is author of job #1.
* **Test Steps**:
  1. Click "Edit" on authored job posting.
  2. Modify salary to "₹9 - 12 LPA".
  3. Click "Save Changes".
* **Expected Result**: Record updated in MySQL; updated salary reflected immediately.
* **Actual Result**: Updated successfully.
* **Status**: **PASS**

### Test Case TC-14: Job Referral Deletion (FR-04)
* **Requirement**: FR-04 – Job Referral Bulletin Board
* **Preconditions**: Alumnus author or Administrator logged in.
* **Test Steps**:
  1. Click "Delete" on job posting, confirm prompt.
* **Expected Result**: Record deleted from MySQL database; job removed from bulletin board.
* **Actual Result**: Deleted successfully.
* **Status**: **PASS**

### Test Case TC-15: Department Endowment Campaigns Display (FR-05)
* **Requirement**: FR-05 – Department Endowment & Pledge Tracking
* **Preconditions**: Pre-seeded projects exist (AI Lab Fund, Scholarships).
* **Test Steps**:
  1. Navigate to "Endowment Pledges" tab.
* **Expected Result**: All active campaigns rendered with project title, department, description, target goal, current amount, and visual percentage progress bar.
* **Actual Result**: Projects rendered with dynamic progress bar fills.
* **Status**: **PASS**

### Test Case TC-16: Endowment Pledge Commitment & Live Aggregate (FR-05)
* **Requirement**: FR-05 – Department Endowment & Pledge Tracking
* **Preconditions**: User logged in; project "AI & Autonomous Robotics Lab Fund" has current amount ₹650,000.
* **Test Steps**:
  1. Click "Make a Pledge" on project #1.
  2. Enter Amount: "50000", Note: "Batch of 2024 alumni donation".
  3. Click "Confirm Financial Pledge".
* **Expected Result**: Pledge saved in `pledges`; project `current_amount` transactionally incremented to ₹700,000; progress bar updates; user confirmation notification generated.
* **Actual Result**: Transaction committed, aggregate updated to ₹700,000, notification created.
* **Status**: **PASS**

### Test Case TC-17: In-App System Notifications (FR-08)
* **Requirement**: FR-08 – Notifications
* **Preconditions**: Notifications exist for logged-in user.
* **Test Steps**:
  1. Navigate to "Alerts" tab.
  2. Click on an unread notification.
* **Expected Result**: Notifications rendered with title, message, and timestamp; clicking notification marks it as read in MySQL via `PUT /api/notifications/{id}/read`.
* **Actual Result**: Unread badge dot clears, notification marked as read.
* **Status**: **PASS**

### Test Case TC-18: User Session Logout & State Reset (FR-06)
* **Requirement**: FR-06 – Authentication
* **Preconditions**: User currently logged in.
* **Test Steps**:
  1. Click Logout icon button in navbar.
* **Expected Result**: `localStorage` cleared, UI reverts to logged-out state, protected action buttons hidden.
* **Actual Result**: State cleared, user redirected to Guest Dashboard.
* **Status**: **PASS**
