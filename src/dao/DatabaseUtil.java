package dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Ket noi CSDL H2 file — tap trung tai mot may chu (file db).
 */
public final class DatabaseUtil {

    private static final String DB_DIR = "data";
    private static final String DB_URL =
            "jdbc:h2:file:./" + DB_DIR + "/pmarket;MODE=MySQL;DEFAULT_NULL_ORDERING=HIGH";

    private static boolean initialized = false;

    private DatabaseUtil() {}

    public static synchronized Connection getConnection() throws SQLException {
        if (!initialized) {
            initDatabase();
            initialized = true;
        }
        return DriverManager.getConnection(DB_URL, "sa", "");
    }

    private static void initDatabase() throws SQLException {
        try {
            Files.createDirectories(Paths.get(DB_DIR));
        } catch (Exception ignored) {
            // thu muc da ton tai
        }
        try (Connection con = DriverManager.getConnection(DB_URL, "sa", "")) {
            SchemaInitializer.init(con);
            SchemaInitializer.seedIfEmpty(con);
        }
    }

    private static boolean isEmpty(Connection con) throws SQLException {
        try (var ps = con.prepareStatement("SELECT COUNT(*) FROM tblAccount");
             var rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    private static void runScript(Statement st, String resourcePath) throws SQLException {
        String sql = loadSql(resourcePath);
        if (sql == null || sql.isBlank()) {
            throw new SQLException("Khong doc duoc file SQL: " + resourcePath);
        }
        for (String part : sql.split(";")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                try {
                    st.execute(trimmed);
                } catch (SQLException ex) {
                    throw new SQLException("Loi SQL: " + trimmed.substring(0, Math.min(80, trimmed.length())) + "... — " + ex.getMessage(), ex);
                }
            }
        }
    }

    private static String resolveSqlPath(String path) {
        if (Files.exists(Paths.get(path))) {
            return path;
        }
        Path inModule = Paths.get("pmarket-swing-java", path);
        if (Files.exists(inModule)) {
            return inModule.toString();
        }
        return path;
    }

    private static String loadSql(String path) {
        String resolved = resolveSqlPath(path);
        try {
            if (Files.exists(Paths.get(resolved))) {
                return Files.readString(Paths.get(resolved), StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {
        }
        try (InputStream in = DatabaseUtil.class.getClassLoader().getResourceAsStream(path)) {
            if (in != null) {
                return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
            }
        } catch (Exception ignored) {
        }
        return "";
    }
}
