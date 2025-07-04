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
    } catch (SQLException e) {
        System.err.println("Error en el servicio al intentar obtener todas las personas: " + e.getMessage());
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
        if (entity == null) {
            throw new IllegalArgumentException("La persona a actualizar no puede ser nula.");
        }
        if (entity.getNombre() == null || entity.getNombre().trim().isEmpty()){
            throw new IllegalArgumentException("El nombre de la persona no puede estar vacío.");
        }
        Optional<Persona> existingPersonaOptional = super.read(entity.getId());
        if (existingPersonaOptional.isEmpty()) {
            throw new RuntimeException("La persona con ID " + entity.getId() + " no fue encontrada para actualizar.");
        }
        
        Persona personaToUpdate = existingPersonaOptional.get();
        personaToUpdate.setNombre(entity.getNombre());
        personaToUpdate.setEdad(entity.getEdad()); 
        return super.update(personaToUpdate); 
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
            System.err.println("Error de servicio creando persona: " + persona.getNombre() + e.getMessage() +"\n");
            e.printStackTrace();
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
