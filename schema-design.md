# Smart Clinic Database Design

## MySQL Database Design

### Table: patients

* id: INT, Primary Key, Auto Increment
* name: VARCHAR(100), Not Null
* email: VARCHAR(100), Unique, Not Null
* phone: VARCHAR(15), Not Null
* address: VARCHAR(255)
* password: VARCHAR(100), Not Null

### Table: doctors

* id: INT, Primary Key, Auto Increment
* name: VARCHAR(100), Not Null
* specialty: VARCHAR(100), Not Null
* email: VARCHAR(100), Unique, Not Null
* phone: VARCHAR(15), Not Null
* password: VARCHAR(100), Not Null

### Table: appointments

* id: INT, Primary Key, Auto Increment
* doctor_id: INT, Foreign Key → doctors(id)
* patient_id: INT, Foreign Key → patients(id)
* appointment_time: DATETIME, Not Null
* status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)

### Table: admin

* id: INT, Primary Key, Auto Increment
* username: VARCHAR(50), Unique, Not Null
* password: VARCHAR(100), Not Null

### Table: doctor_available_times

* id: INT, Primary Key, Auto Increment
* doctor_id: INT, Foreign Key → doctors(id)
* available_time: VARCHAR(50), Not Null

### Notes

* Emails should remain unique for doctors and patients.
* Appointment records should remain stored for future history tracking.
* Doctors should not have overlapping appointments.
* Foreign key relationships help maintain data integrity.

---

## MongoDB Collection Design

### Collection: prescriptions

```json
{
  "_id": "ObjectId('64abc123456')",
  "patientName": "John Smith",
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take 1 tablet every 6 hours.",
  "refillCount": 2,
  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street"
  },
  "tags": ["fever", "painkiller"],
  "createdAt": "2026-05-29T10:00:00"
}
```

### Notes

* MongoDB is used because prescription records may contain flexible fields.
* Embedded pharmacy information reduces additional queries.
* Tags can help categorize prescriptions.
* The schema can evolve easily if new fields are needed later.
