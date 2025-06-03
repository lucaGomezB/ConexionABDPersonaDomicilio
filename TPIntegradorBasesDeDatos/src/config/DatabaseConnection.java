package config; 
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        //Con estos valores se accede a la BD local, se pueden modificar acá o en el .env de ser necesario.
        URL = "jdbc:mysql://localhost:3306/integradorprog2?useSSL=false&serverTimezone=UTC";
        USER = "root";
        PASSWORD = "";

        try {
            Dotenv dotenv = Dotenv.load();
            String dbUrl = dotenv.get("DB_URL");
            if (dbUrl != null && !dbUrl.isEmpty()) {
                URL = dbUrl;
            }
            String dbUser = dotenv.get("DB_USER");
            if (dbUser != null && !dbUser.isEmpty()) {
                USER = dbUser;
            }
            String dbPassword = dotenv.get("DB_USER_PASSWORD");
            if (dbPassword != null) {
                PASSWORD = dbPassword;
            }

            System.out.println("DatabaseConnection: Credenciales cargadas desde .env.");

        } catch (DotenvException ex) {
            System.err.println("DatabaseConnection: Advertencia: No se encontró el archivo .env o está mal configurado.");
            System.err.println("(Asegúrate de que esté en el directorio raíz de tu proyecto, al lado de src, test, etc.)");
            System.err.println("DatabaseConnection: Intentando usar variables de entorno del sistema o valores por defecto.");

            String sysUrl = System.getenv("DB_URL");
            if (sysUrl != null && !sysUrl.isEmpty()) {
                URL = sysUrl;
            }
            String sysUser = System.getenv("DB_USER");
            if (sysUser != null && !sysUser.isEmpty()) {
                USER = sysUser;
            }
            String sysPassword = System.getenv("DB_USER_PASSWORD");
            if (sysPassword != null) {
                PASSWORD = sysPassword;
            }
            System.out.println("DatabaseConnection: Usando valores cargados o por defecto: URL=" + URL + ", USER=" + USER);
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("DatabaseConnection: Driver JDBC cargado correctamente.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("DatabaseConnection: Excepción fatal: No se encontró el driver JDBC de MySQL. Asegúrate de tener 'mysql-connector-java' en tus dependencias (classpath).", e);
        }

        if (URL == null || URL.isEmpty() || USER == null || USER.isEmpty() || PASSWORD == null) {
            throw new RuntimeException("DatabaseConnection: Error fatal: La configuración de la base de datos es incompleta o inválida después de todos los intentos de carga. (URL, USER, PASSWORD).");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}