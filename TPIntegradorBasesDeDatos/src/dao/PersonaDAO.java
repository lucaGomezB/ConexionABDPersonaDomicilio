// PersonaDAO.java (re-visiting for clarity)
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

public class PersonaDAO extends BaseDAO<Persona, Integer> {
    private DomicilioDAO domicilioDao;
    public PersonaDAO(DomicilioDAO domicilioDao) {
        super(Persona.class, "persona", "id");
        this.domicilioDao = domicilioDao;
    }

    @Override
    protected Persona mapResultSetToObject(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        int edad = rs.getInt("edad");
        int idDomicilio = rs.getInt("id_domicilio");
        Domicilio domicilio = null;
        
        try {
            domicilio = this.domicilioDao.findById(idDomicilio);
        } catch (SQLException e) {
            System.err.println("Error retrieving Domicilio for Persona ID " + id + ": " + e.getMessage());
        }
        return new Persona(id, nombre, edad, domicilio);
    }

    public Persona findById(Integer id) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Persona persona = null;

        // This is the custom SQL query with the JOIN
        String sql = "SELECT p.id, p.nombre, p.edad, " +
                     "d.id AS domicilio_id, d.localidad, d.provincia " +
                     "FROM " + tableName + " p " + 
                     "JOIN integradorprog2.domicilio d ON p.id_domicilio = d.id " +
                     "WHERE p." + idColumnName + " = ?"; 

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id); 
            rs = ps.executeQuery();

            if (rs.next()) {
                persona = mapResultSetToObject(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding Persona by ID: " + e.getMessage());
            throw e;
        } finally {
            closeResources(ps, rs, conn);
        }
        return persona;
    }
    @Override
    protected PreparedStatement prepareStatementForInsert(Connection conn, Persona entity) throws SQLException {
        int domicilioId = entity.getDomicilio().getId();; 

        String sql = "INSERT INTO " + tableName + " (nombre, edad, id_domicilio) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, entity.getNombre());
        ps.setInt(2, entity.getEdad());
        ps.setInt(3, domicilioId);
        return ps;
    }

    @Override
    protected PreparedStatement prepareStatementForUpdate(Connection conn, Persona entity) throws SQLException {
        int domicilioId;
        if (entity.getDomicilio() != null && entity.getDomicilio().getId() == 0) {
            this.domicilioDao.insertWithConnection(conn, entity.getDomicilio());
            domicilioId = entity.getDomicilio().getId();
        } else if (entity.getDomicilio() != null && entity.getDomicilio().getId() != 0) {
            this.domicilioDao.updateWithConnection(conn, entity.getDomicilio());
            domicilioId = entity.getDomicilio().getId();
        } else {
            throw new IllegalArgumentException("Persona debe tener un Domicilio para actualizarla.");
        }
        String sql = "UPDATE " + tableName + " SET nombre = ?, edad = ?, id_domicilio = ? WHERE " + idColumnName + " = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, entity.getNombre());
        ps.setInt(2, entity.getEdad());
        ps.setInt(3, domicilioId);
        ps.setInt(4, entity.getId());
        return ps;
    }
    
    @Override
    public List<Persona> findAll(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT p.id, p.nombre, p.edad, " +
                     "d.id AS domicilio_id, d.localidad, d.provincia " + 
                     "FROM " + tableName + " p " + 
                     "JOIN integradorprog2.domicilio d ON p.id_domicilio = d.id";

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                personas.add(mapResultSetToObject(rs));
            }
        } catch(SQLException e){
            System.err.println("Error al listar todas las entidades de " + tableName + ": " + e.getMessage());
            throw new RuntimeException("Error en la base de datos al listar entidades.", e);
        } finally {
            closeResources(ps, rs, conn);
        }
        return personas;
    }
    
    
}
