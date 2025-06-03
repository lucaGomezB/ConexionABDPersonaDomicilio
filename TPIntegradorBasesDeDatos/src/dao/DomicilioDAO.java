// DomicilioDAO.java
package dao; // Adjust package as needed

import config.DatabaseConnection;
import model.Domicilio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DomicilioDAO extends BaseDAO<Domicilio, Integer> {

    public DomicilioDAO() {
        super(Domicilio.class, "domicilio", "id");
    }

    @Override
    protected Domicilio mapResultSetToObject(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String localidad = rs.getString("localidad");
        String provincia = rs.getString("provincia");
        return new Domicilio(id, localidad, provincia);
    }

    // Existing insert method (might still use its own connection)
    @Override
    protected PreparedStatement prepareStatementForInsert(Connection conn, Domicilio entity) throws SQLException {
        String sql = "INSERT INTO " + tableName + " (localidad, provincia) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, entity.getLocalidad());
        ps.setString(2, entity.getProvincia());
        return ps;
    }

    public void insertWithConnection(Connection conn, Domicilio entity) throws SQLException {
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;
        try {
            ps = prepareStatementForInsert(conn, entity);
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getInt(1));
                }
            }
        } finally {
            closeResources(ps, generatedKeys, null);
        }
    }

    @Override
    protected PreparedStatement prepareStatementForUpdate(Connection conn, Domicilio entity) throws SQLException {
        String sql = "UPDATE " + tableName + " SET localidad = ?, provincia = ? WHERE " + idColumnName + " = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, entity.getLocalidad());
        ps.setString(2, entity.getProvincia());
        ps.setInt(3, entity.getId());
        return ps;
    }

    // New method for updating Domicilio using an existing connection
    public void updateWithConnection(Connection conn, Domicilio entity) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = prepareStatementForUpdate(conn, entity);
            ps.executeUpdate();
        } finally {
            closeResources(ps, null, null);
        }
    }
    
    public Domicilio findById(Integer id) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Domicilio domicilio = null;
        String sql = "SELECT * FROM "+tableName+" WHERE "+id+" = ?";
        try{
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.next()){
                domicilio = mapResultSetToObject(rs);
            }
        }catch (SQLException e){
            System.err.println("Error encontrando Domicilio por ID: "+e.getMessage());
        }finally{
            closeResources(ps,rs,conn);
        }
        return domicilio;
    }
}