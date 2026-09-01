package com.sunrisedental.web.model;

public class DentistViewModel {

    private int dentistId;

    private String dentistCode;

    private String fullName;

    private String specialization;

    private String contactNumber;

    private String email;

    private boolean active;


    public DentistViewModel() {
    }


    public int getDentistId() {
        return dentistId;
    }


    public String getDentistCode() {
        return dentistCode;
    }


    public String getFullName() {
        return fullName;
    }


    public String getSpecialization() {
        return specialization;
    }


    public String getContactNumber() {
        return contactNumber;
    }


    public String getEmail() {
        return email;
    }


    public boolean isActive() {
        return active;
    }


    /**
     * User-friendly status text for JSP pages.
     */
    public String getStatusText() {

        return active
                ? "Active"
                : "Inactive";
    }
}