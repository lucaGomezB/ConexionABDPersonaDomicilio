package dao;

import java.sql.SQLException; // Import SQLException
import java.util.List;
import java.util.Optional;

public interface GenericDAO<T, ID> {
    T create(T entity) throws SQLException; // Added throws SQLException
    
    Optional<T> read(ID id) throws SQLException; // Added throws SQLException
    
    T update(T entity) throws SQLException; // Added throws SQLException
    
    void delete(ID id) throws SQLException; // Added throws SQLException
}