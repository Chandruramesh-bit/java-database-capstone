# User Story Template

**Title:** Admin Login

*As an admin, I want to log into the portal using my username and password, so that I can securely manage the platform.*

**Acceptance Criteria:**

1. Admin can enter username and password.
2. System validates admin credentials.
3. Admin is redirected to dashboard after successful login.

**Priority:** High
**Story Points:** 3

**Notes:**

* Invalid credentials should display an error message.

---

**Title:** Admin Logout

*As an admin, I want to log out of the portal, so that I can protect system access.*

**Acceptance Criteria:**

1. Admin can click logout button.
2. Session is terminated successfully.
3. User is redirected to login page.

**Priority:** Medium
**Story Points:** 2

**Notes:**

* Logout should clear active session data.

---

**Title:** Add Doctors

*As an admin, I want to add doctors to the portal, so that patients can book appointments.*

**Acceptance Criteria:**

1. Admin can enter doctor details.
2. System stores doctor information.
3. Added doctor appears in doctor list.

**Priority:** High
**Story Points:** 4

**Notes:**

* Email validation should be applied.

---

**Title:** Patient Appointment Booking

*As a patient, I want to book an appointment with a doctor, so that I can receive medical consultation.*

**Acceptance Criteria:**

1. Patient can select doctor and time slot.
2. Appointment is saved successfully.
3. Confirmation message is displayed.

**Priority:** High
**Story Points:** 5

**Notes:**

* Time slot should not allow double booking.

---

**Title:** Doctor View Appointments

*As a doctor, I want to view my appointments, so that I can manage my schedule.*

**Acceptance Criteria:**

1. Doctor can log into portal.
2. Doctor can view appointment list.
3. Upcoming appointments are displayed correctly.

**Priority:** High
**Story Points:** 3

**Notes:**

* Appointment list should refresh automatically.
