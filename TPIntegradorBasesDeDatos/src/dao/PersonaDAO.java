package dao;

import config.DatabaseConnection;
import model.Persona;
import model.Domicilio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Added for findByID return type

public class PersonaDAO extends BaseDAO<Persona, Integer> {
    private DomicilioDAO domicilioDao;

    public PersonaDAO(DomicilioDAO domicilioDao) {
        super(Persona.class, "persona", "id");
        this.domicilioDao = domicilioDao;
    }

    @Override
    protected Persona mapResultSetToObject(ResultSet rs) throws SQLException {
        int personaId = rs.getInt("id");
        String nombre = rs.getString("nombre");
        int edad = rs.getInt("edad");
        int domicilioId = rs.getInt("id_domicilio"); // Corrected column name from 'domicilio_id' to 'id_domicilio'
                                                     // based on the SQL in prepareStatementForInsert/Update
        String localidad = rs.getString("localidad");
        String provincia = rs.getString("provincia");

        Domicilio domicilio = null;
        if (domicilioId > 0) { // Check if a valid domicilio_id exists
            domicilio = new Domicilio(domicilioId, localidad, provincia);
        } else {
            System.err.println("Advertencia: Domicilio ID = 0 o es inválido para Persona ID: " + personaId + ". Domicilio será null.");
        }
        return new Persona(personaId, nombre, edad, domicilio);
    }

    public Optional<Persona> read(Connection conn, Integer id) throws SQLException { // Method name changed to 'read'
        String sql = "SELECT p.id, p.nombre, p.edad, d.id AS domicilio_id, d.localidad, d.provincia " +
                     "FROM persona p INNER JOIN domicilio d ON p.id_domicilio = d.id " +
                     "WHERE p.id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Persona persona = null;
        try {
            // Use the connection passed from the service layer (for transactional consistency)
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                persona = mapResultSetToObject(rs);
            }
            return Optional.ofNullable(persona); // Return Optional
        } catch (SQLException e) {
            System.err.println("Error al encontrar entidad Persona por ID: " + id + ": " + e.getMessage());
            throw e; // Re-throw to be handled by the service layer's rollback mechanism
        } finally {
            // IMPORTANT: Do NOT close 'conn' here as it's managed by the service layer.
            // Only close the PreparedStatement and ResultSet.
            closeResources(pstmt, rs, null); 
        }
    }

    @Override
    protected PreparedStatement prepareStatementForInsert(Connection conn, Persona entity) throws SQLException {
        // Handle Domicilio creation/update before Persona insertion
        if (entity.getDomicilio() == null) {
            throw new IllegalArgumentException("Persona must have a Domicilio for insertion.");
        }

        // Use the transactional methods from DomicilioDAO (now present in BaseDAO)
        if (entity.getDomicilio().getId() == 0) {
            this.domicilioDao.insert(conn, entity.getDomicilio()); // Use transactional insert
        } else {
            // This is the problematic line. Now BaseDAO has update(Connection conn, T entity)
            this.domicilioDao.update(conn, entity.getDomicilio()); // Use transactional update
        }

        int domicilioId = entity.getDomicilio().getId(); // Ensure ID is updated if inserted
        String sql = "INSERT INTO " + tableName + " (nombre, edad, id_domicilio) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, entity.getNombre());
        ps.setInt(2, entity.getEdad());
        ps.setInt(3, domicilioId);
        return ps;
    }

    @Override
    protected PreparedStatement prepareStatementForUpdate(Connection conn, Persona entity) throws SQLException {
        if (entity.getDomicilio() == null) {
            throw new IllegalArgumentException("Persona must have a Domicilio for update.");
        }

        // Use the transactional methods from DomicilioDAO
        if (entity.getDomicilio().getId() == 0) {
            this.domicilioDao.insert(conn, entity.getDomicilio()); // If Domicilio has no ID, insert it
        } else {
            this.domicilioDao.update(conn, entity.getDomicilio()); // Update existing Domicilio
        }

        int domicilioId = entity.getDomicilio().getId();
        String sql = "UPDATE " + tableName + " SET nombre = ?, edad = ?, id_domicilio = ? WHERE " + idColumnName + " = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, entity.getNombre());
        ps.setInt(2, entity.getEdad());
        ps.setInt(3, domicilioId);
        ps.setInt(4, entity.getId());
        return ps;
    }

    @Override
    public List<Persona> findAll() throws SQLException {
        List<Persona> personas = new ArrayList<>();
        // Modified SQL to join with Domicilio table to fetch complete Domicilio data
        String sql = "SELECT p.id, p.nombre, p.edad, d.id AS id_domicilio, d.localidad, d.provincia " +
                     "FROM persona p INNER JOIN domicilio d ON p.id_domicilio = d.id";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                personas.add(mapResultSetToObject(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar todas las entidades Persona: " + e.getMessage());
            throw e;
        } finally {
            closeResources(pstmt, rs, conn);
        }
        return personas;
    }
}