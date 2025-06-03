package dao;
/**
 *
 * @author lucaGomezB
 */
import java.util.List;
import java.util.Optional;
public interface GenericDAO<T, ID> {
    T create(T entity);//
    
    Optional<T> read(ID id); //Optional contiene la entidad si la encuentra, sino queda vacío.
    
    T update(T entity);
    
    void delete(ID id);
    
    List<T> findAll();
}
