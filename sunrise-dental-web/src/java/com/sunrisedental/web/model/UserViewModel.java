package com.sunrisedental.web.model;

public class UserViewModel {

    private int userId;

    private String username;

    private String fullName;

    private String role;

    private boolean active;

    /*
     * Keep API date value as text in the Web ViewModel.
     * The REST API returns ISO date/time text and no
     * additional Gson LocalDateTime adapter is required.
     */
    private String createdAt;


    public UserViewModel() {
    }


    public int getUserId() {
        return userId;
    }


    public String getUsername() {
        return username;
    }


    public String getFullName() {
        return fullName;
    }


    public String getRole() {
        return role;
    }


    public boolean isActive() {
        return active;
    }


    public String getCreatedAt() {
        return createdAt;
    }


    /*
     * ============================================================
     * PRESENTATION HELPERS
     * ============================================================
     */

    public String getStatusText() {

        return active
                ? "Active"
                : "Inactive";
    }


    public String getRoleText() {

        if (role == null
                || role.isBlank()) {

            return "Not assigned";
        }

        if ("ADMIN".equalsIgnoreCase(role)) {
            return "Administrator";
        }

        if ("RECEPTIONIST".equalsIgnoreCase(role)) {
            return "Receptionist";
        }

        return role;
    }


    public boolean isAdmin() {

        return role != null
                && "ADMIN".equalsIgnoreCase(role);
    }


    public boolean isReceptionist() {

        return role != null
                && "RECEPTIONIST".equalsIgnoreCase(role);
    }


    public String getCreatedAtText() {

        if (createdAt == null
                || createdAt.isBlank()) {

            return "Not available";
        }

        /*
         * API value example:
         * 2026-08-31T15:38:39
         *
         * Keep formatting simple and safe for JSP display.
         */
        return createdAt.replace(
                "T",
                " "
        );
    }
}