-- ==========================================================
-- SMART ALUMNI NETWORK SYSTEM
-- MySQL 8.0 Comprehensive Sample Seed Data
-- ==========================================================

USE smart_alumni;

-- ----------------------------------------------------------
-- 1. DEPARTMENTS (5 Academic Departments)
-- ----------------------------------------------------------
INSERT INTO departments (dept_id, dept_code, dept_name) VALUES
(1, 'CSE-AI', 'Computer Science & Engineering (Artificial Intelligence)'),
(2, 'CSE', 'Computer Science & Engineering'),
(3, 'IT', 'Information Technology'),
(4, 'ECE', 'Electronics & Communication Engineering'),
(5, 'MECH', 'Mechanical Engineering');

-- ----------------------------------------------------------
-- 2. VERIFIED GRADUATES (Institutional Registry for Verification)
-- ----------------------------------------------------------
INSERT INTO verified_graduates (grad_id, register_no, full_name, dept_id, batch_year, degree, is_registered) VALUES
(1, '44731001', 'Shiela Soundararajan', 1, 2024, 'B.E. CSE AI', TRUE),
(2, '44731002', 'Arun Kumar', 1, 2023, 'B.E. CSE AI', TRUE),
(3, '44731003', 'Priya Sundaram', 2, 2022, 'B.E. CSE', TRUE),
(4, '44731004', 'Rajesh Venkat', 3, 2023, 'B.Tech IT', TRUE),
(5, '44731005', 'Ananya Mehra', 4, 2021, 'B.E. ECE', TRUE),
(6, '44731006', 'Karthik Subramanian', 1, 2024, 'B.E. CSE AI', FALSE),
(7, '44731007', 'Divya Ramachandran', 2, 2023, 'B.E. CSE', FALSE),
(8, '44731008', 'Gautam Siddharth', 3, 2022, 'B.Tech IT', FALSE),
(9, '44731009', 'Naveen Prasath', 5, 2020, 'B.E. MECH', FALSE),
(10, '44731010', 'Sowmya Krishnan', 1, 2024, 'B.E. CSE AI', FALSE);

-- ----------------------------------------------------------
-- 3. USERS (Pre-seeded Accounts with SHA-256 Hashes)
-- Demo salt: 'fixed_demo_salt_2026'
-- Admin pass: 'admin123'  => 578350c98686cb0437b860cc113d1e9c0c74680ea4b3e56439acf2d3b043fc48
-- Alumni pass: 'alumni123' => d78c9f1ea3f1cfd9cd69d003192304889b2df236797403e2b918b267ec5b5f71
-- Student pass: 'student123' => 32a0336134ee1d7051f42a8434a8f7cdc19717cbd060f8836c76b6580e08dccd
-- ----------------------------------------------------------
INSERT INTO users (user_id, email, password_hash, salt, role, status) VALUES
-- Admin
(1, 'admin@gmail.com', '578350c98686cb0437b860cc113d1e9c0c74680ea4b3e56439acf2d3b043fc48', 'fixed_demo_salt_2026', 'Admin', 'Active'),

-- Alumni
(2, 'shiela@gmail.com', 'd78c9f1ea3f1cfd9cd69d003192304889b2df236797403e2b918b267ec5b5f71', 'fixed_demo_salt_2026', 'Alumni', 'Active'),
(3, 'arun.kumar@gmail.com', 'd78c9f1ea3f1cfd9cd69d003192304889b2df236797403e2b918b267ec5b5f71', 'fixed_demo_salt_2026', 'Alumni', 'Active'),
(4, 'priya.s@gmail.com', 'd78c9f1ea3f1cfd9cd69d003192304889b2df236797403e2b918b267ec5b5f71', 'fixed_demo_salt_2026', 'Alumni', 'Active'),
(5, 'rajesh.v@gmail.com', 'd78c9f1ea3f1cfd9cd69d003192304889b2df236797403e2b918b267ec5b5f71', 'fixed_demo_salt_2026', 'Alumni', 'Active'),
(6, 'ananya.m@gmail.com', 'd78c9f1ea3f1cfd9cd69d003192304889b2df236797403e2b918b267ec5b5f71', 'fixed_demo_salt_2026', 'Alumni', 'Active'),

-- Students
(7, 'kiruthi@gmail.com', '32a0336134ee1d7051f42a8434a8f7cdc19717cbd060f8836c76b6580e08dccd', 'fixed_demo_salt_2026', 'Student', 'Active'),
(8, 'imalini@gmail.com', '32a0336134ee1d7051f42a8434a8f7cdc19717cbd060f8836c76b6580e08dccd', 'fixed_demo_salt_2026', 'Student', 'Active'),
(9, 'deepak.s@gmail.com', '32a0336134ee1d7051f42a8434a8f7cdc19717cbd060f8836c76b6580e08dccd', 'fixed_demo_salt_2026', 'Student', 'Active'),
(10, 'kavitha.r@gmail.com', '32a0336134ee1d7051f42a8434a8f7cdc19717cbd060f8836c76b6580e08dccd', 'fixed_demo_salt_2026', 'Student', 'Active'),
(11, 'vijay.k@gmail.com', '32a0336134ee1d7051f42a8434a8f7cdc19717cbd060f8836c76b6580e08dccd', 'fixed_demo_salt_2026', 'Student', 'Active');

-- ----------------------------------------------------------
-- 4. ALUMNI PROFILES
-- ----------------------------------------------------------
INSERT INTO alumni (alumni_id, user_id, register_no, full_name, dept_id, batch_year, company, designation, location, phone, linkedin_url, is_mentor_available) VALUES
(1, 2, '44731001', 'Shiela Soundararajan', 1, 2024, 'Tata Consultancy Services (TCS)', 'Senior AI Engineer', 'Chennai, India', '7338876444', 'https://linkedin.com/in/shiela-ai', TRUE),
(2, 3, '44731002', 'Arun Kumar', 1, 2023, 'Amazon AWS', 'Software Development Engineer II', 'Bengaluru, India', '9840123456', 'https://linkedin.com/in/arunkumar-aws', TRUE),
(3, 4, '44731003', 'Priya Sundaram', 2, 2022, 'Microsoft', 'Technical Product Manager', 'Hyderabad, India', '9123456780', 'https://linkedin.com/in/priya-sundaram', TRUE),
(4, 5, '44731004', 'Rajesh Venkat', 3, 2023, 'Zoho Corporation', 'Lead Cloud Solutions Architect', 'Chennai, India', '9789012345', 'https://linkedin.com/in/rajesh-cloud', FALSE),
(5, 6, '44731005', 'Ananya Mehra', 4, 2021, 'Qualcomm Technologies', 'Staff Hardware Engineer', 'Bengaluru, India', '9443210987', 'https://linkedin.com/in/ananya-qualcomm', TRUE);

-- ----------------------------------------------------------
-- 5. STUDENT PROFILES
-- ----------------------------------------------------------
INSERT INTO students (student_id, user_id, register_no, full_name, dept_id, current_year, phone, bio) VALUES
(1, 7, '44732001', 'Kiruthika Ramanathan', 1, 3, '7338876444', 'Passionate about Deep Learning, Generative AI, and autonomous systems. Aspiring ML researcher.'),
(2, 8, '44732002', 'Imalini Devi', 1, 4, '9876543210', 'Final year CSE AI student specializing in NLP, LLM fine-tuning, and full-stack web architectures.'),
(3, 9, '44732003', 'Deepak Selvam', 2, 3, '9876501234', 'Core Java and backend systems enthusiast preparing for product firm campus placements.'),
(4, 10, '44732004', 'Kavitha Rajan', 3, 2, '9123451234', 'Second year IT student interested in cloud computing, DevOps, and microservice architectures.'),
(5, 11, '44732005', 'Vijay Krishnan', 4, 3, '9445678901', 'Embedded systems and IoT developer looking for hardware-software co-design mentorship.');

-- ----------------------------------------------------------
-- 6. JOBS (Alumni Job & Referral Postings)
-- ----------------------------------------------------------
INSERT INTO jobs (job_id, alumni_id, job_title, company, location, job_type, salary_range, description, required_skills, application_link) VALUES
(1, 1, 'Associate AI Engineer (Campus Referral)', 'Tata Consultancy Services (TCS)', 'Chennai / Hybrid', 'Full-Time', '₹7.5 - 10 LPA', 'Looking for motivated graduate developers with strong foundations in Python, PyTorch, and NLP for our Innovation Labs.', 'Python, PyTorch, SQL, REST APIs', 'https://tcs.com/careers/referral/ai-2026'),
(2, 2, 'Software Development Engineer I (SDE-1)', 'Amazon AWS', 'Bengaluru, India', 'Full-Time', '₹22 - 28 LPA', 'Join the AWS Cloud Infrastructure team. You will build highly scalable distributed systems handling billions of requests.', 'Java, Data Structures, System Design, AWS', 'https://amazon.jobs/en/jobs/258901'),
(3, 3, 'Associate Product Manager - Azure Cloud', 'Microsoft', 'Hyderabad, India', 'Full-Time', '₹18 - 24 LPA', 'Drive roadmap definitions and collaborate with engineering teams for enterprise AI integration on Azure.', 'Product Analytics, Agile, Cloud Fundamentals', 'https://careers.microsoft.com/ref/99214'),
(4, 4, 'Full Stack Cloud Developer', 'Zoho Corporation', 'Chennai, India', 'Full-Time', '₹8 - 12 LPA', 'Build resilient enterprise web services using modern JavaScript, Java backend microservices, and MySQL databases.', 'Java, JavaScript, MySQL, Linux, Git', 'https://zoho.com/careers/jobs/cloud-dev'),
(5, 5, 'Embedded Firmware Engineer', 'Qualcomm Technologies', 'Bengaluru, India', 'Full-Time', '₹14 - 18 LPA', 'Develop low-level drivers and BSP firmware for next-gen 5G Snapdragon modem chipsets.', 'C/C++, RTOS, ARM Architecture, Device Drivers', 'https://qualcomm.wd5.myworkdayjobs.com/careers'),
(6, 1, 'Machine Learning Intern (Summer 2026)', 'TCS Research & Innovation', 'Chennai, India', 'Internship', '₹35,000 / month', 'Hands-on summer internship working on Computer Vision and Transformer model compression for edge devices.', 'Python, OpenCV, PyTorch, NumPy', 'https://tcs.com/research/internships');

-- ----------------------------------------------------------
-- 7. MENTORSHIP SESSIONS (Scheduled & Requested Sessions)
-- ----------------------------------------------------------
INSERT INTO mentorship_sessions (session_id, student_id, alumni_id, topic, message, preferred_date, preferred_time, status, response_notes) VALUES
(1, 1, 1, 'AI/ML Career Roadmap & Resume Review', 'Hello Shiela ma\'am, I am preparing for AI engineer roles and would value your feedback on my GitHub portfolio and resume.', '2026-09-25', '06:30 PM - 07:15 PM', 'Accepted', 'Looking forward to meeting! Please have your GitHub links and resume PDF ready.'),
(2, 2, 2, 'AWS SDE Interview Preparation & Coding rounds', 'Hi Arun sir, I have cleared the preliminary OA for cloud roles and would like tips on Amazon Leadership Principles.', '2026-09-28', '07:00 PM - 08:00 PM', 'Accepted', 'Accepted! Be ready to discuss your recent distributed systems project in detail.'),
(3, 3, 3, 'Transition from SDE to Product Management', 'Hi Priya ma\'am, I am interested in technical product roles and would love guidance on APM preparation tracks.', '2026-10-02', '05:00 PM - 05:45 PM', 'Pending', NULL),
(4, 4, 4, 'Cloud Microservices & Distributed Architecture', 'Hello Rajesh sir, seeking advice on designing resilient backend architectures with JDBC and relational stores.', '2026-10-05', '06:00 PM - 06:45 PM', 'Pending', NULL),
(5, 5, 5, 'Hardware-Software Co-Design in Qualcomm', 'Seeking guidance on embedded C benchmarks and preparing for chipset company technical rounds.', '2026-09-20', '04:00 PM - 04:45 PM', 'Completed', 'Conducted 45-minute mock interview. Student demonstrated great grasp on interrupt routines.');

-- ----------------------------------------------------------
-- 8. ENDOWMENT PROJECTS (Department Infrastructure & Scholarships)
-- ----------------------------------------------------------
INSERT INTO endowment_projects (project_id, dept_id, title, description, target_amount, current_amount, status) VALUES
(1, 1, 'AI & Autonomous Robotics Center of Excellence', 'State-of-the-art GPU computing clusters (NVIDIA H100s) and robotics development kits for undergraduate AI research.', 1500000.00, 650000.00, 'Active'),
(2, 1, 'SIST Women in AI & Tech Scholarship Endowment', 'Merit-cum-means tuition fee sponsorships for high-achieving female engineering students in CSE AI.', 500000.00, 250000.00, 'Active'),
(3, 2, 'Cloud Computing & High-Performance Computing Lab', 'Dedicated server infrastructure and high-throughput networking racks for hands-on distributed systems training.', 1000000.00, 420000.00, 'Active'),
(4, 3, 'Open Source Innovation & Cyber Defense Sandbox', 'A safe, isolated hardware environment for security auditing, penetration testing workshops, and hackathons.', 750000.00, 310000.00, 'Active'),
(5, 4, 'VLSI Chip Design & IoT Fab Prototyping Unit', 'Precision test equipment, oscilloscopes, and FPGA development benches for student silicon design projects.', 1200000.00, 800000.00, 'Active');

-- ----------------------------------------------------------
-- 9. PLEDGES (Alumni Financial Commitments)
-- ----------------------------------------------------------
INSERT INTO pledges (pledge_id, project_id, user_id, pledge_amount, payment_status, notes) VALUES
(1, 1, 2, 100000.00, 'Fulfilled', 'Proud to support our CSE AI juniors with state-of-the-art GPU resources.'),
(2, 1, 3, 150000.00, 'Fulfilled', 'Contribution towards high-performance computing clusters.'),
(3, 2, 4, 100000.00, 'Fulfilled', 'Empowering female engineers through targeted tuition sponsorships.'),
(4, 3, 5, 120000.00, 'Fulfilled', 'Towards server hardware acquisition for cloud distributed labs.'),
(5, 5, 6, 200000.00, 'Fulfilled', 'Dedicated for student FPGA prototyping development kits.'),
(6, 1, 2, 50000.00, 'Pledged', 'Quarterly recurring pledge commitment for student lab upkeep.'),
(7, 2, 3, 50000.00, 'Pledged', 'Annual scholarship contribution commitment.'),
(8, 4, 4, 60000.00, 'Pledged', 'Security software license contribution.');

-- ----------------------------------------------------------
-- 10. NOTIFICATIONS (System Activity Alerts)
-- ----------------------------------------------------------
INSERT INTO notifications (notification_id, user_id, title, message, is_read) VALUES
(1, 7, 'Mentorship Session Confirmed', 'Shiela Soundararajan has accepted your mentorship request on AI/ML Career Roadmap for Sep 25, 2026.', FALSE),
(2, 8, 'Mentorship Session Confirmed', 'Arun Kumar has accepted your mentorship request on AWS SDE Preparation for Sep 28, 2026.', FALSE),
(3, 2, 'New Mentorship Request Received', 'Kiruthika Ramanathan requested a 1-on-1 mentorship session on AI/ML Career Roadmap.', TRUE),
(4, 7, 'New Job Referral Posted', 'A new referral for Associate AI Engineer at TCS was posted by alumnus Shiela Soundararajan.', TRUE),
(5, 1, 'Endowment Goal Milestone Reached', 'AI & Autonomous Robotics Center has crossed 40% of its target funding goal.', FALSE),
(6, 3, 'Pledge Acknowledgment', 'Thank you for your generous pledge of ₹150,000 towards the AI Center of Excellence.', TRUE);
