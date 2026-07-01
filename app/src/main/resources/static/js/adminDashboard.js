/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";
import { openModal, closeModal } from "./components/modals.js";

const content = document.getElementById("content");
const searchBar = document.getElementById("searchBar");
const filterTime = document.getElementById("filterTime");
const filterSpecialty = document.getElementById("filterSpecialty");

const addDoctorBtn = document.getElementById("addDocBtn");

if (addDoctorBtn) {
    addDoctorBtn.addEventListener("click", () => {
        openModal("addDoctor");
    });
}

document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();
});

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error(error);
    }
}

if (searchBar) {
    searchBar.addEventListener("input", filterDoctorsOnChange);
}

if (filterTime) {
    filterTime.addEventListener("change", filterDoctorsOnChange);
}

if (filterSpecialty) {
    filterSpecialty.addEventListener("change", filterDoctorsOnChange);
}

async function filterDoctorsOnChange() {

    const name = searchBar.value || null;
    const time = filterTime.value || null;
    const specialty = filterSpecialty.value || null;

    try {

        const data = await filterDoctors(name, time, specialty);

        if (data.doctors && data.doctors.length > 0) {

            renderDoctorCards(data.doctors);

        } else {

            content.innerHTML = "<h3>No doctors found with the given filters.</h3>";

        }

    } catch (error) {

        console.error(error);
        alert("Error while filtering doctors.");

    }
}

function renderDoctorCards(doctors) {

    content.innerHTML = "";

    doctors.forEach((doctor) => {
        content.appendChild(createDoctorCard(doctor));
    });

}

window.adminAddDoctor = async function () {

    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const phone = document.getElementById("phone").value;
    const password = document.getElementById("password").value;
    const specialty = document.getElementById("specialty").value;
    const availableTimes = document.getElementById("availableTimes").value
        .split(",")
        .map(time => time.trim());

    const token = localStorage.getItem("token");

    if (!token) {
        alert("Authentication required.");
        return;
    }

    const doctor = {
        name,
        email,
        phone,
        password,
        specialty,
        availableTimes
    };

    const result = await saveDoctor(doctor, token);

    if (result.success) {

        alert(result.message);
        closeModal();
        window.location.reload();

    } else {

        alert(result.message);

    }
};