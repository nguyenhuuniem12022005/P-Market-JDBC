package dao;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Ket noi CSDL H2 file. DAO chi mo connection toi database da duoc tao bang
 * database/schema.sql va database/seed.sql.
 */
public final class DatabaseUtil {

    private static final String DB_DIR = "data";
   private static final String DB_URL =
            "jdbc:h2:file:./" + DB_DIR + "/pmarket;MODE=MySQL;DEFAULT_NULL_ORDERING=HIGH";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private DatabaseUtil() {}

    public static Connection getConnection() throws SQLException {
        try {
            Files.createDirectories(Paths.get(DB_DIR));
        } catch (Exception ignored) {
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
