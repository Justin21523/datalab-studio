package com.justin.datalab.dataset;

import com.justin.datalab.shared.enums.ColumnType;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Incrementally narrows the logical type of a single column as values are
 * observed. Each non-blank value can only remove candidate types, so after
 * scanning all values the most specific surviving type is chosen.
 *
 * <p>Type specificity (most to least): BOOLEAN, INTEGER, DECIMAL, DATE,
 * DATETIME, STRING. INTEGER is preferred over DECIMAL because every integer is
 * also a valid decimal.</p>
 */
final class ColumnTypeInferer {

    private boolean couldBeInteger = true;
    private boolean couldBeDecimal = true;
    private boolean couldBeBoolean = true;
    private boolean couldBeDate = true;
    private boolean couldBeDateTime = true;

    private boolean sawValue = false;
    private boolean sawMissing = false;

    void observe(String raw) {
        if (raw == null || raw.isBlank()) {
            sawMissing = true;
            return;
        }
        String value = raw.trim();
        sawValue = true;
        couldBeInteger &= isInteger(value);
        couldBeDecimal &= isDecimal(value);
        couldBeBoolean &= isBoolean(value);
        couldBeDate &= isDate(value);
        couldBeDateTime &= isDateTime(value);
    }

    ColumnType resolveType() {
        if (!sawValue) {
            return ColumnType.UNKNOWN;
        }
        if (couldBeBoolean) {
            return ColumnType.BOOLEAN;
        }
        if (couldBeInteger) {
            return ColumnType.INTEGER;
        }
        if (couldBeDecimal) {
            return ColumnType.DECIMAL;
        }
        if (couldBeDate) {
            return ColumnType.DATE;
        }
        if (couldBeDateTime) {
            return ColumnType.DATETIME;
        }
        return ColumnType.STRING;
    }

    boolean sawMissing() {
        return sawMissing;
    }

    private static boolean isInteger(String value) {
        if (!value.matches("[+-]?\\d+")) {
            return false;
        }
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDecimal(String value) {
        // Reject NaN/Infinity/hex that Double.parseDouble would otherwise accept.
        if (!value.matches("[+-]?(\\d+\\.?\\d*|\\.\\d+)([eE][+-]?\\d+)?")) {
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isBoolean(String value) {
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
    }

    private static boolean isDate(String value) {
        try {
            LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isDateTime(String value) {
        try {
            LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
