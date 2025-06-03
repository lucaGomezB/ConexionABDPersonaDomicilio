package service;

import config.DatabaseConnection;
import dao.DomicilioDAO;
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
    public DomicilioService(DomicilioDAO dao) {
        super(dao);
    }

    @Override
    public Domicilio create(Domicilio domicilio) {
        if (domicilio == null) {
            throw new IllegalArgumentException("El domicilio a crear no puede ser nulo.");
        }
        if (domicilio.getLocalidad() == null || domicilio.getLocalidad().trim().isEmpty() ||
            domicilio.getProvincia() == null || domicilio.getProvincia().trim().isEmpty()) {
            throw new IllegalArgumentException("La provincia y localidad no pueden estar vacías.");
        }
        return super.create(domicilio);
    }

    @Override
    public Domicilio update(Domicilio domicilio) {
        if (domicilio == null){
            throw new IllegalArgumentException("El domicilio a actualizar no puede ser nulo y debe tener un ID válido.");
        }
        if (domicilio.getLocalidad() == null || domicilio.getLocalidad().trim().isEmpty()) {
            throw new IllegalArgumentException("La localidad no puede estar vacía.");
        }
        if (domicilio.getProvincia() == null || domicilio.getProvincia().trim().isEmpty()) {
            throw new IllegalArgumentException("La provincia no puede estar vacía.");
        }
        return super.update(domicilio);
    }
    
    @Override 
    public Optional<Domicilio> read(Integer id) { 
        if (id == null || id <= 0) { 
            throw new IllegalArgumentException("El ID para leer no puede ser nulo o inválido.");
        }
        return super.read(id); 
    }

    @Override
    public List<Domicilio> findAll() {
        return super.findAll();
    }

    @Override
    public void delete(Integer id) {
        super.delete(id); 
    }
}