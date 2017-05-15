package ru.nsu.fit.g14203.popov.util;

public final class SQLHelper {

    public static String dropTableIfExists(String tableName) {
        return String.format(
                "BEGIN" +
                "   EXECUTE IMMEDIATE 'DROP TABLE %s';" +
                "EXCEPTION" +
                "   WHEN OTHERS THEN" +
                "      IF SQLCODE != -942 THEN" +
                "         RAISE;" +
                "      END IF;" +
                "END;", tableName
        );
    }
}
