package com.sunrisedental.web.model;

public class PatientViewModel {

    private int patientId;
    private String patientCode;
    private String fullName;
    private String address;
    private String contactNumber;
    private String email;
    private String dateOfBirth;
    private String gender;

    public PatientViewModel() {
    }

    public int getPatientId() {
        return patientId;
    }

    public String getPatientCode() {
        return patientCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getDisplayGender() {

        if (gender == null || gender.isBlank()) {
            return "-";
        }

        return gender.substring(0, 1).toUpperCase()
                + gender.substring(1).toLowerCase();
    }
}