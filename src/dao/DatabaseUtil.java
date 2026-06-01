package dao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Ket noi CSDL H2 file. DAO chi mo connection toi database da duoc tao bang
 * database/schema.sql va database/seed.sql.
 */
public final class DatabaseUtil {

    private static final Path DB_DIR = resolveProjectDir().resolve("data");
    private static final String DB_URL = "jdbc:h2:file:"
            + DB_DIR.resolve("pmarket").toAbsolutePath().normalize().toString().replace('\\', '/')
            + ";MODE=MySQL;DEFAULT_NULL_ORDERING=HIGH";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private DatabaseUtil() {}

    public static Connection getConnection() throws SQLException {
        try {
            Files.createDirectories(DB_DIR);
        } catch (Exception ignored) {
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private static Path resolveProjectDir() {
        Path cwd = Paths.get("").toAbsolutePath().normalize();
        if (Files.exists(cwd.resolve("database").resolve("schema.sql"))) {
            return cwd;
        }
        Path nestedProject = cwd.resolve("cnpm");
        if (Files.exists(nestedProject.resolve("database").resolve("schema.sql"))) {
            return nestedProject;
        }
        return cwd;
    }
}
