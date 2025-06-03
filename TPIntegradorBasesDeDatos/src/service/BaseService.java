package service;

import config.DatabaseConnection; 
import dao.BaseDAO;
import java.util.List;
import java.util.Optional;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract base class for services, providing common CRUD operations
 * with transactional control and consistent exception handling.
 *
 * @param <T> The type of the entity (e.g., Persona, Domicilio).
 * @param <ID> The type of the entity's ID (e.g., Integer).
 */
public abstract class BaseService<T, ID> implements GenericService<T, ID> {

    protected static final Logger LOGGER = Logger.getLogger(BaseService.class.getName());

    protected BaseDAO<T, ID> dao;

    public BaseService(BaseDAO<T, ID> dao) {
        this.dao = dao;
    }

    @Override
    public T create(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad a crear no puede ser nula.");
        }
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            T createdEntity = dao.insert(conn, entity);
            conn.commit();
            return createdEntity;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de SQL al crear la entidad: " + e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    LOGGER.log(Level.SEVERE, "Falló el rollback al crear la entidad.", rbEx);
                }
            }
            throw new RuntimeException("Error en la operación de base de datos al crear la entidad.", e);
        } catch (IllegalArgumentException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    LOGGER.log(Level.SEVERE, "Falló el rollback por error de argumento al crear la entidad.", rbEx);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    LOGGER.log(Level.SEVERE, "Error al cerrar la conexión después de crear la entidad.", closeEx);
                }
            }
        }
    }

    @Override
    public Optional<T> read(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID para leer no puede ser nulo.");
        }
        try {
            return dao.read(id); 
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de SQL al leer la entidad con ID: " + id + ". " + e.getMessage(), e);
            throw new RuntimeException("Error en la base de datos al leer entidad con ID: " + id, e);
        }
    }

    @Override
    public T update(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad a actualizar no puede ser nula.");
        }
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            T updatedEntity = dao.update(conn, entity);
            conn.commit();
            return updatedEntity;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de SQL al actualizar la entidad: " + e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    LOGGER.log(Level.SEVERE, "Falló el rollback al actualizar la entidad.", rbEx);
                }
            }
            throw new RuntimeException("Error en la operación de base de datos al actualizar la entidad.", e);
        } catch (IllegalArgumentException e) { // Catch validation errors
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    LOGGER.log(Level.SEVERE, "Falló el rollback por error de argumento al actualizar la entidad.", rbEx);
                }
            }
            throw e; // Re-throw validation error
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    LOGGER.log(Level.SEVERE, "Error al cerrar la conexión después de actualizar la entidad.", closeEx);
                }
            }
        }
    }

    @Override
    public void delete(ID id) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            // Assuming ID is Integer, as per previous discussions for Persona/Domicilio
            dao.delete(conn, (Integer) id); // dao.delete(Connection conn, Integer id)
            conn.commit();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de SQL al borrar la entidad con ID: " + id + ". " + e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    LOGGER.log(Level.SEVERE, "Falló el rollback al borrar con el ID: " + id, rollbackEx);
                }
            }
            throw new RuntimeException("Error en la operación de base de datos al eliminar la entidad con ID: " + id, e);
        } catch (ClassCastException e) { // Catch if ID is not Integer, for the cast (Integer) id
            LOGGER.log(Level.SEVERE, "Error de tipo al borrar la entidad con ID: " + id + ". Se esperaba Integer.", e);
            throw new IllegalArgumentException("Tipo de ID no soportado para la eliminación. Se esperaba Integer.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    LOGGER.log(Level.SEVERE, "Error al cerrar la conexión después de eliminar la entidad con ID: " + id, closeEx);
                }
            }
        }
    }

    @Override
    public List<T> findAll() {
        try {
            return dao.findAll(); // dao.findAll() throws SQLException
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error de SQL al listar todas las entidades: " + e.getMessage(), e);
            throw new RuntimeException("Error en la base de datos al listar todas las entidades.", e);
        }
    }
    
    public T findById(ID id) {
        Optional<T> entityOptional = read(id); 
        if (entityOptional.isEmpty()) {
            throw new RuntimeException("La entidad con ID " + id + " no fue encontrada.");
        }
        return entityOptional.get();
    }
}