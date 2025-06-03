package service;
/**
 *
 * @author lucaGomezB
 */
import config.DatabaseConnection;
import dao.BaseDAO;
import dao.GenericDAO;
import java.util.List;
import java.util.Optional;
import java.sql.SQLException;
import java.sql.Connection;

public abstract class BaseService<T, ID> implements GenericService<T, ID> {
    protected BaseDAO<T, ID> dao;
    
    public BaseService(BaseDAO<T, ID> dao) {
        this.dao = dao;
    }

    @Override
    public T create(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad a crear no puede ser nula.");
        }
        return dao.create(entity);
    }

    @Override
    public Optional<T> read(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID para leer no puede ser nulo.");
        }
        return dao.read(id);
    }

    @Override
    public T update(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad a actualizar no puede ser nula.");
        }
        return dao.update(entity);
    }

    @Override
    public void delete(ID id) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            dao.delete(conn, (Integer) id);
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error de servicio al borrar la entidad con un ID.");
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Falló el rollback al borrar con el ID");
                }
            }
            throw new RuntimeException("Failed to delete entity with ID: " + id + e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); 
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    public List<T> findAll() {
        return dao.findAll();
    }
}