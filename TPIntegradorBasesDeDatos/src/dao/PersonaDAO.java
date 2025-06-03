// PersonaDAO.java (re-visiting for clarity)
package dao;

import java.sql.ResultSetMetaData;
import config.DatabaseConnection;
import model.Persona;
import model.Domicilio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        int domicilioId = rs.getInt("domicilio_id");
        String localidad = rs.getString("localidad");
        String provincia = rs.getString("provincia");
        Domicilio domicilio = null;
        if (domicilioId > 0) {
            domicilio = new Domicilio(domicilioId, localidad, provincia);
        } else {
            System.err.println("Advertencia: Domicilio ID = 0 o es inválido para ID: " + personaId + ". Domicilio será null.");
        }

        return new Persona(personaId, nombre, edad, domicilio);
    }

    @Override
    public Persona findByID(Integer id) throws SQLException {
        String sql = "SELECT p.id, p.nombre, p.edad, d.id AS domicilio_id, d.localidad, d.provincia " +
                     "FROM persona p INNER JOIN domicilio d ON p.id_domicilio = d.id " +
                     "WHERE p.id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Persona persona = null;
        try {
            conn = DatabaseConnection.getConnection(); 
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                persona = mapResultSetToObject(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al encontrar entidad Persona por ID: " + id + ": " + e.getMessage());
            throw e;
        } finally {
            closeResources(pstmt, rs, conn);
        }
        return persona;
    }

    @Override
    protected PreparedStatement prepareStatementForInsert(Connection conn, Persona entity) throws SQLException {
        if (entity.getDomicilio().getId() == 0) {
            this.domicilioDao.insert(conn, entity.getDomicilio());
        } else {
            this.domicilioDao.updateWithConnection(conn, entity.getDomicilio());
        }
        int domicilioId = entity.getDomicilio().getId();
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
        } else if (entity.getDomicilio().getId() == 0) {
            this.domicilioDao.insert(conn, entity.getDomicilio());
        } else {
            this.domicilioDao.updateWithConnection(conn, entity.getDomicilio());
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
        String sql = "SELECT p.id, p.nombre, p.edad, d.id AS domicilio_id, d.localidad, d.provincia " +
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
