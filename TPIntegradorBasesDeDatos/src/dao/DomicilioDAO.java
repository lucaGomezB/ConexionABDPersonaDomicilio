package dao;

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

    @Override
    protected PreparedStatement prepareStatementForInsert(Connection conn, Domicilio entity) throws SQLException {
        String sql = "INSERT INTO " + tableName + " (localidad, provincia) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, entity.getLocalidad());
        ps.setString(2, entity.getProvincia());
        return ps;
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
}