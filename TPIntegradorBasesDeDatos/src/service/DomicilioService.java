package service;

import config.DatabaseConnection;
import dao.DomicilioDAO;
import dao.GenericDAO;
import model.Domicilio;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author lucaGomezB
 */
public class DomicilioService extends BaseService<Domicilio, Integer>{
    private final DomicilioDAO domicilioDAO;
    public DomicilioService(DomicilioDAO dao) {
        super(dao);
        this.domicilioDAO = dao;
    }
    
    @Override
    public Domicilio create(Domicilio domicilio){
        String localidadNormalizada = domicilio.getLocalidad();
        String provinciaNormalizada = domicilio.getProvincia();
        if (domicilio == null){
            throw new IllegalArgumentException("El domicilio a crear no puede ser nulo.");
        }
        if(domicilio.getLocalidad() == null || localidadNormalizada.trim().isEmpty() || domicilio.getLocalidad() == null || provinciaNormalizada.trim().isEmpty()){
            throw new IllegalArgumentException("La provincia y localidad no pueden estar vacías.");
        }
        Connection conn = null;
        try{
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            if(domicilio.getId() <= 0){
                throw new IllegalArgumentException("El ID de categoria no es válido.");
            }
            Domicilio domicilioCreado = dao.create(domicilio);
            conn.commit();
            return domicilioCreado;
        }catch(SQLException e){
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Error al realizar rollback: " + rbEx.getMessage());
                }
            }
            System.err.println("Error al crear el domicilio: " + e.getMessage());
            throw new RuntimeException("Error en la operación de base de datos al crear el producto.", e);
        } catch (IllegalArgumentException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Error al realizar rollback: " + rbEx.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error al cerrar la conexión: " + closeEx.getMessage());
                }
            }
        }
    }
    
    @Override
    public Domicilio update(Domicilio domicilio){
        Optional<Domicilio> existingCategory = super.read(domicilio.getId());
        if(domicilio.getLocalidad().isEmpty() || domicilio.getLocalidad() == null){
            throw new IllegalArgumentException("La localidad no puede estar vacía.");
        }
        if(domicilio.getProvincia().isEmpty() || domicilio.getProvincia() == null){
            throw new IllegalArgumentException("La provincia no puede estar vacía.");
        }
        Domicilio domicilioToUpdate = existingCategory.get();
        domicilioToUpdate.setLocalidad(domicilio.getLocalidad());
        domicilioToUpdate.setProvincia(domicilio.getProvincia());
        return dao.update(domicilioToUpdate);
    }
}
