package config;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
/**
 *
 * @author lucaGomezB <lucaGomezB at https://lucagomezb.github.io/Luca-Gomez/>
 */
public class DatabaseConnection {
    //El .env contiene los datos necesarios para conectarse a un servidor local (datos por defecto, como la URL de localhost, root, password vacío, etc.)
    private static String URL; 
    private static String PASSWORD;
    private static String USER;
    
    static{
        /*Acá se intentarán cargar los valores desde un .env
        para no exponer las credenciales de ingreso a la BD sin querer.*/
        try {
            Dotenv dotenv = Dotenv.load();
            URL = (dotenv != null) ? dotenv.get("DB_URL") : System.getenv("DB_URL");
            USER = (dotenv != null) ? dotenv.get("DB_USER") : System.getenv("DB_USER");
            PASSWORD = (dotenv != null) ? dotenv.get("DB_USER_PASSWORD") : System.getenv("DB_USER_PASSWORD");
        } catch (io.github.cdimascio.dotenv.DotenvException ex) { // Corrected: DotenvException (lowercase 'e')
            System.out.println("Advertencia: No se encontró el archivo .env o está mal configurado.\n(Dejalo en el directorio root, al lado de src, test, etc...)\nIntentando usar valores por defecto o variables de entorno del sistema.");
        }
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            throw new RuntimeException("Excepción : No se encontró el driver JDBC. ",e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Porfavor ingrese su usuario de MySQL, o X para usar root : ");
        String usuario = sc.nextLine();
        System.out.println("Porfavor ingrese su contraseña de MySQL, o X para usar la contraseña por defecto");
        String contrasena = sc.nextLine();
        if(!usuario.equalsIgnoreCase("X")){
            USER = usuario;
        }else if(!contrasena.equalsIgnoreCase("X")){
            PASSWORD = contrasena;
        }
        if(URL == null || URL.isEmpty() || USER == null || USER.isEmpty() || PASSWORD == null){
            throw new SQLException("Configuración de la base de datos incompleta o inválida (porfavor revise los datos.)");
        }
        sc.close();
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }
}