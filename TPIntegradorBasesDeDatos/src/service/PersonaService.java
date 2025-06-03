package service;

import config.DatabaseConnection;
import dao.GenericDAO;
import dao.PersonaDAO;
import java.util.List;
import java.util.Optional;
import model.Persona;
import java.sql.SQLException;
import java.sql.Connection;

/**
 *
 * @author lucaGomezB
 */

public class PersonaService extends BaseService<Persona,Integer> {
    private final PersonaDAO personaDao;
    public PersonaService(PersonaDAO dao) {
        super(dao);
        this.personaDao = dao;
    }

    @Override
    public List<Persona> findAll() {
        try {
            return personaDao.findAll();
        } catch (RuntimeException e) { 
            throw new RuntimeException("No se han podido conseguir todas las personas.", e);
        }
    }

    @Override
    public void delete(Integer id) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            personaDao.delete(conn, id);
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error de servicio borrando Persona por ID: " + id);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Falló el rollback al borrar Persona por ID: " + id);
                }
            }
            throw new RuntimeException("No se pudo borrar la persona con ID: " + id, e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("No se pudo cerrar la conexión luego de borrar la personapor ID " + id);
                }
            }
        }
    }


    @Override
    public Persona update(Persona entity) {
        if (entity.getNombre() == null || entity.getNombre().trim().isEmpty()){
            throw new IllegalArgumentException("El nombre de la persona no pueda estar vacío.");
        }
        return dao.update(entity);
    }

    @Override
    public Persona create(Persona persona) {
        if (persona.getId() != 0) {
            throw new IllegalArgumentException("No se puede crear una persona con un ID existente, porfavor actualice con update.");
        }
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            personaDao.insert(conn, persona);
            conn.commit();
            return persona;
        } catch (SQLException e) {
            System.err.println("Error de servicio creando persona: " + persona.getNombre());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Fallo de rollback creando persona: " + persona.getNombre());
                }
            }
            throw new RuntimeException("No se pudo crear la persona: " + persona.getNombre(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("No se pudo cerrar la conexión después de crear a persona:  " + persona.getNombre());
                }
            }
        }
    }
}
