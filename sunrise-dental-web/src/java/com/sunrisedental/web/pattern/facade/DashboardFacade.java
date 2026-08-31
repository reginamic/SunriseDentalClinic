package com.sunrisedental.web.pattern.facade;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.sunrisedental.web.client.ApiClient;
import com.sunrisedental.web.model.DashboardSummaryViewModel;

import java.io.IOException;
import java.math.BigDecimal;

public class DashboardFacade {

    private final ApiClient apiClient;

    /*
     * Production constructor.
     */
    public DashboardFacade() {

        this(
                new ApiClient()
        );
    }

    /*
     * Dependency-injection constructor.
     *
     * Useful for testing and keeps the Facade
     * independent from direct object creation.
     */
    public DashboardFacade(
            ApiClient apiClient) {

        if (apiClient == null) {

            throw new IllegalArgumentException(
                    "ApiClient is required."
            );
        }

        this.apiClient =
                apiClient;
    }

    /*
     * ============================================================
     * FACADE OPERATION
     * ============================================================
     *
     * The DashboardServlet needs only this single method.
     *
     * Internally the Facade coordinates multiple REST subsystems:
     *
     * - Patients
     * - Dentists
     * - Treatments
     * - Appointments
     * - Billing
     *
     * The controller does not need to understand how those
     * individual APIs are called or aggregated.
     */
    public DashboardSummaryViewModel
            getDashboardSummary()
            throws IOException, InterruptedException {

        DashboardSummaryViewModel summary =
                new DashboardSummaryViewModel();

        loadPatientSummary(
                summary
        );

        loadDentistSummary(
                summary
        );

        loadTreatmentSummary(
                summary
        );

        loadAppointmentSummary(
                summary
        );

        loadBillingSummary(
                summary
        );

        return summary;
    }

    /*
     * ============================================================
     * PATIENT SUBSYSTEM
     * ============================================================
     */
    private void loadPatientSummary(
            DashboardSummaryViewModel summary)
            throws IOException, InterruptedException {

        JsonArray patients =
                getArray(
                        "/api/patients"
                );

        summary.setTotalPatients(
                patients.size()
        );
    }

    /*
     * ============================================================
     * DENTIST SUBSYSTEM
     * ============================================================
     */
    private void loadDentistSummary(
            DashboardSummaryViewModel summary)
            throws IOException, InterruptedException {

        JsonArray dentists =
                getArray(
                        "/api/dentists"
                );

        int activeDentists = 0;

        for (JsonElement element : dentists) {

            if (!element.isJsonObject()) {
                continue;
            }

            JsonObject dentist =
                    element.getAsJsonObject();

            if (readBoolean(
                    dentist,
                    "active"
            )) {

                activeDentists++;
            }
        }

        summary.setTotalDentists(
                dentists.size()
        );

        summary.setActiveDentists(
                activeDentists
        );
    }

    /*
     * ============================================================
     * TREATMENT SUBSYSTEM
     * ============================================================
     */
    private void loadTreatmentSummary(
            DashboardSummaryViewModel summary)
            throws IOException, InterruptedException {

        JsonArray treatments =
                getArray(
                        "/api/treatments"
                );

        int activeTreatments = 0;

        for (JsonElement element : treatments) {

            if (!element.isJsonObject()) {
                continue;
            }

            JsonObject treatment =
                    element.getAsJsonObject();

            if (readBoolean(
                    treatment,
                    "active"
            )) {

                activeTreatments++;
            }
        }

        summary.setTotalTreatments(
                treatments.size()
        );

        summary.setActiveTreatments(
                activeTreatments
        );
    }

    /*
     * ============================================================
     * APPOINTMENT SUBSYSTEM
     * ============================================================
     */
    private void loadAppointmentSummary(
            DashboardSummaryViewModel summary)
            throws IOException, InterruptedException {

        JsonArray appointments =
                getArray(
                        "/api/appointments"
                );

        int scheduled = 0;
        int completed = 0;
        int cancelled = 0;

        for (JsonElement element : appointments) {

            if (!element.isJsonObject()) {
                continue;
            }

            JsonObject appointment =
                    element.getAsJsonObject();

            String status =
                    readString(
                            appointment,
                            "status"
                    );

            if ("SCHEDULED".equalsIgnoreCase(
                    status
            )) {

                scheduled++;

            } else if (
                    "COMPLETED".equalsIgnoreCase(
                            status
                    )
            ) {

                completed++;

            } else if (
                    "CANCELLED".equalsIgnoreCase(
                            status
                    )
            ) {

                cancelled++;
            }
        }

        summary.setTotalAppointments(
                appointments.size()
        );

        summary.setScheduledAppointments(
                scheduled
        );

        summary.setCompletedAppointments(
                completed
        );

        summary.setCancelledAppointments(
                cancelled
        );
    }

    /*
     * ============================================================
     * BILLING SUBSYSTEM
     * ============================================================
     */
    private void loadBillingSummary(
            DashboardSummaryViewModel summary)
            throws IOException, InterruptedException {

        JsonArray bills =
                getArray(
                        "/api/bills"
                );

        int paidBills = 0;
        int unpaidBills = 0;

        BigDecimal totalBilled =
                BigDecimal.ZERO;

        BigDecimal totalPaid =
                BigDecimal.ZERO;

        BigDecimal outstanding =
                BigDecimal.ZERO;

        for (JsonElement element : bills) {

            if (!element.isJsonObject()) {
                continue;
            }

            JsonObject bill =
                    element.getAsJsonObject();

            String paymentStatus =
                    readString(
                            bill,
                            "paymentStatus"
                    );

            BigDecimal totalAmount =
                    readBigDecimal(
                            bill,
                            "totalAmount"
                    );

            totalBilled =
                    totalBilled.add(
                            totalAmount
                    );

            if ("PAID".equalsIgnoreCase(
                    paymentStatus
            )) {

                paidBills++;

                totalPaid =
                        totalPaid.add(
                                totalAmount
                        );

            } else if (
                    "UNPAID".equalsIgnoreCase(
                            paymentStatus
                    )
            ) {

                unpaidBills++;

                outstanding =
                        outstanding.add(
                                totalAmount
                        );
            }
        }

        summary.setTotalBills(
                bills.size()
        );

        summary.setPaidBills(
                paidBills
        );

        summary.setUnpaidBills(
                unpaidBills
        );

        summary.setTotalBilled(
                totalBilled
        );

        summary.setTotalPaid(
                totalPaid
        );

        summary.setOutstandingAmount(
                outstanding
        );
    }

    /*
     * ============================================================
     * REST / JSON HELPER
     * ============================================================
     */
    private JsonArray getArray(
            String endpoint)
            throws IOException, InterruptedException {

        String json =
                apiClient.get(
                        endpoint
                );

        if (json == null
                || json.isBlank()) {

            return new JsonArray();
        }

        JsonElement element =
                JsonParser.parseString(
                        json
                );

        if (!element.isJsonArray()) {

            throw new IOException(
                    "Unexpected API response from "
                    + endpoint
                    + ". Expected a JSON array."
            );
        }

        return element.getAsJsonArray();
    }

    /*
     * ============================================================
     * SAFE JSON VALUE READERS
     * ============================================================
     */

    private boolean readBoolean(
            JsonObject object,
            String property) {

        if (object == null
                || !object.has(property)
                || object.get(property).isJsonNull()) {

            return false;
        }

        try {

            return object
                    .get(property)
                    .getAsBoolean();

        } catch (Exception exception) {

            return false;
        }
    }

    private String readString(
            JsonObject object,
            String property) {

        if (object == null
                || !object.has(property)
                || object.get(property).isJsonNull()) {

            return "";
        }

        try {

            return object
                    .get(property)
                    .getAsString();

        } catch (Exception exception) {

            return "";
        }
    }

    private BigDecimal readBigDecimal(
            JsonObject object,
            String property) {

        if (object == null
                || !object.has(property)
                || object.get(property).isJsonNull()) {

            return BigDecimal.ZERO;
        }

        try {

            return object
                    .get(property)
                    .getAsBigDecimal();

        } catch (Exception exception) {

            return BigDecimal.ZERO;
        }
    }
}