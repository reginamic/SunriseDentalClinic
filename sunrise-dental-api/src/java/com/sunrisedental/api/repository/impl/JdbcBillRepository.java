package com.sunrisedental.api.repository.impl;

import com.sunrisedental.api.config.DatabaseConnectionManager;
import com.sunrisedental.api.model.Bill;
import com.sunrisedental.api.model.BillItem;
import com.sunrisedental.api.model.BillItemType;
import com.sunrisedental.api.model.BillStatus;
import com.sunrisedental.api.repository.BillRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBillRepository implements BillRepository {

    private final DatabaseConnectionManager connectionManager;

    public JdbcBillRepository() {
        this.connectionManager =
                DatabaseConnectionManager.getInstance();
    }

    @Override
    public List<Bill> findAll() throws SQLException {

        String sql = """
                SELECT
                    bill_id,
                    bill_number,
                    appointment_id,
                    subtotal,
                    additional_charges,
                    discount_amount,
                    total_amount,
                    payment_status,
                    generated_by,
                    generated_at
                FROM bills
                ORDER BY generated_at DESC
                """;

        List<Bill> bills = new ArrayList<>();

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet resultSet =
                    statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Bill bill =
                        mapBill(resultSet);

                loadBillItems(
                        connection,
                        bill
                );

                bills.add(bill);
            }
        }

        return bills;
    }

    @Override
    public Optional<Bill> findById(
            int billId)
            throws SQLException {

        String sql = """
                SELECT
                    bill_id,
                    bill_number,
                    appointment_id,
                    subtotal,
                    additional_charges,
                    discount_amount,
                    total_amount,
                    payment_status,
                    generated_by,
                    generated_at
                FROM bills
                WHERE bill_id = ?
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    billId
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    Bill bill =
                            mapBill(resultSet);

                    loadBillItems(
                            connection,
                            bill
                    );

                    return Optional.of(bill);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Bill> findByNumber(
            String billNumber)
            throws SQLException {

        String sql = """
                SELECT
                    bill_id,
                    bill_number,
                    appointment_id,
                    subtotal,
                    additional_charges,
                    discount_amount,
                    total_amount,
                    payment_status,
                    generated_by,
                    generated_at
                FROM bills
                WHERE bill_number = ?
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    billNumber
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    Bill bill =
                            mapBill(resultSet);

                    loadBillItems(
                            connection,
                            bill
                    );

                    return Optional.of(bill);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Bill> findByAppointmentId(
            int appointmentId)
            throws SQLException {

        String sql = """
                SELECT
                    bill_id,
                    bill_number,
                    appointment_id,
                    subtotal,
                    additional_charges,
                    discount_amount,
                    total_amount,
                    payment_status,
                    generated_by,
                    generated_at
                FROM bills
                WHERE appointment_id = ?
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    appointmentId
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    Bill bill =
                            mapBill(resultSet);

                    loadBillItems(
                            connection,
                            bill
                    );

                    return Optional.of(bill);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public Bill save(
            Bill bill)
            throws SQLException {

        Connection connection = null;

        try {

            connection =
                    connectionManager.getConnection();

            /*
             * Start one database transaction.
             */
            connection.setAutoCommit(false);

            /*
             * First save the parent bill.
             */
            insertBill(
                    connection,
                    bill
            );

            /*
             * Then save all child bill items
             * using the same transaction.
             */
            insertBillItems(
                    connection,
                    bill
            );

            /*
             * Both operations succeeded.
             */
            connection.commit();

            return bill;

        } catch (SQLException exception) {

            /*
             * If either the bill or any bill item fails,
             * undo the complete billing operation.
             */
            if (connection != null) {

                try {

                    connection.rollback();

                } catch (SQLException rollbackException) {

                    exception.addSuppressed(
                            rollbackException
                    );
                }
            }

            throw exception;

        } finally {

            if (connection != null) {

                try {
                    connection.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // Connection is about to close.
                }

                try {
                    connection.close();
                } catch (SQLException ignored) {
                    // Do not hide the original exception.
                }
            }
        }
    }

    @Override
    public boolean updatePaymentStatus(
            int billId,
            BillStatus paymentStatus)
            throws SQLException {

        String sql = """
                UPDATE bills
                SET payment_status = ?
                WHERE bill_id = ?
                """;

        try (
            Connection connection =
                    connectionManager.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    paymentStatus.name()
            );

            statement.setInt(
                    2,
                    billId
            );

            return statement.executeUpdate() > 0;
        }
    }

    private void insertBill(
            Connection connection,
            Bill bill)
            throws SQLException {

        String sql = """
                INSERT INTO bills
                (
                    bill_number,
                    appointment_id,
                    subtotal,
                    additional_charges,
                    discount_amount,
                    total_amount,
                    payment_status,
                    generated_by
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
            PreparedStatement statement =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    )
        ) {

            statement.setString(
                    1,
                    bill.getBillNumber()
            );

            statement.setInt(
                    2,
                    bill.getAppointmentId()
            );

            statement.setBigDecimal(
                    3,
                    bill.getSubtotal()
            );

            statement.setBigDecimal(
                    4,
                    bill.getAdditionalCharges()
            );

            statement.setBigDecimal(
                    5,
                    bill.getDiscountAmount()
            );

            statement.setBigDecimal(
                    6,
                    bill.getTotalAmount()
            );

            statement.setString(
                    7,
                    bill.getPaymentStatus().name()
            );

            statement.setInt(
                    8,
                    bill.getGeneratedBy()
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {

                throw new SQLException(
                        "Creating bill failed. No rows affected."
                );
            }

            try (
                ResultSet generatedKeys =
                        statement.getGeneratedKeys()
            ) {

                if (!generatedKeys.next()) {

                    throw new SQLException(
                            "Creating bill failed. "
                            + "No generated ID returned."
                    );
                }

                bill.setBillId(
                        generatedKeys.getInt(1)
                );
            }
        }
    }

    private void insertBillItems(
            Connection connection,
            Bill bill)
            throws SQLException {

        /*
         * IMPORTANT:
         * The real MySQL column is line_total,
         * not total_price.
         */
        String sql = """
                INSERT INTO bill_items
                (
                    bill_id,
                    item_name,
                    item_type,
                    quantity,
                    unit_price,
                    line_total
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            for (BillItem item : bill.getItems()) {

                item.setBillId(
                        bill.getBillId()
                );

                statement.setInt(
                        1,
                        bill.getBillId()
                );

                statement.setString(
                        2,
                        item.getItemName()
                );

                statement.setString(
                        3,
                        item.getItemType().name()
                );

                statement.setInt(
                        4,
                        item.getQuantity()
                );

                statement.setBigDecimal(
                        5,
                        item.getUnitPrice()
                );

                statement.setBigDecimal(
                        6,
                        item.getTotalPrice()
                );

                statement.addBatch();
            }

            int[] results =
                    statement.executeBatch();

            for (int result : results) {

                if (result == Statement.EXECUTE_FAILED) {

                    throw new SQLException(
                            "Saving one or more bill items failed."
                    );
                }
            }
        }
    }

    private void loadBillItems(
            Connection connection,
            Bill bill)
            throws SQLException {

        /*
         * Again, the database column is line_total.
         */
        String sql = """
                SELECT
                    bill_item_id,
                    bill_id,
                    item_name,
                    item_type,
                    quantity,
                    unit_price,
                    line_total
                FROM bill_items
                WHERE bill_id = ?
                ORDER BY bill_item_id
                """;

        try (
            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    bill.getBillId()
            );

            try (
                ResultSet resultSet =
                        statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    BillItem item =
                            new BillItem();

                    item.setBillItemId(
                            resultSet.getInt(
                                    "bill_item_id"
                            )
                    );

                    item.setBillId(
                            resultSet.getInt(
                                    "bill_id"
                            )
                    );

                    item.setItemName(
                            resultSet.getString(
                                    "item_name"
                            )
                    );

                    item.setItemType(
                            BillItemType.fromString(
                                    resultSet.getString(
                                            "item_type"
                                    )
                            )
                    );

                    item.setQuantity(
                            resultSet.getInt(
                                    "quantity"
                            )
                    );

                    item.setUnitPrice(
                            resultSet.getBigDecimal(
                                    "unit_price"
                            )
                    );

                    /*
                     * CORRECTED:
                     * Java property = totalPrice
                     * MySQL column  = line_total
                     */
                    item.setTotalPrice(
                            resultSet.getBigDecimal(
                                    "line_total"
                            )
                    );

                    bill.addItem(item);
                }
            }
        }
    }

    private Bill mapBill(
            ResultSet resultSet)
            throws SQLException {

        Bill bill =
                new Bill();

        bill.setBillId(
                resultSet.getInt(
                        "bill_id"
                )
        );

        bill.setBillNumber(
                resultSet.getString(
                        "bill_number"
                )
        );

        bill.setAppointmentId(
                resultSet.getInt(
                        "appointment_id"
                )
        );

        bill.setSubtotal(
                resultSet.getBigDecimal(
                        "subtotal"
                )
        );

        bill.setAdditionalCharges(
                resultSet.getBigDecimal(
                        "additional_charges"
                )
        );

        bill.setDiscountAmount(
                resultSet.getBigDecimal(
                        "discount_amount"
                )
        );

        bill.setTotalAmount(
                resultSet.getBigDecimal(
                        "total_amount"
                )
        );

        bill.setPaymentStatus(
                BillStatus.fromString(
                        resultSet.getString(
                                "payment_status"
                        )
                )
        );

        bill.setGeneratedBy(
                resultSet.getInt(
                        "generated_by"
                )
        );

        if (resultSet.getTimestamp(
                "generated_at") != null) {

            bill.setGeneratedAt(
                    resultSet
                            .getTimestamp(
                                    "generated_at"
                            )
                            .toLocalDateTime()
            );
        }

        return bill;
    }
}