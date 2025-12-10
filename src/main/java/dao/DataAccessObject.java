package dao;

import java.sql.SQLException;
import java.util.List;

/**
 * A generic Data Access Object (DAO) interface that defines the standard CRUD
 * (Create, Read, Update, Delete) operations for a given model type.
 *
 * @param <T> The model type for which the DAO will be implemented.
 */
public interface DataAccessObject<T> {

    /**
     * Adds a new entity to the database.
     *
     * @param t The entity to add.
     * @throws SQLException If a database access error occurs.
     */
    void add(T t) throws SQLException;

    /**
     * Updates an existing entity in the database.
     *
     * @param t The entity to update.
     * @throws SQLException If a database access error occurs.
     */
    void update(T t) throws SQLException;

    /**
     * Deletes an entity from the database by its ID.
     *
     * @param id The ID of the entity to delete.
     * @throws SQLException If a database access error occurs.
     */
    void delete(int id) throws SQLException;

    /**
     * Retrieves an entity from the database by its ID.
     *
     * @param id The ID of the entity to retrieve.
     * @return The entity with the specified ID, or null if not found.
     * @throws SQLException If a database access error occurs.
     */
    T getById(int id) throws SQLException;

    /**
     * Retrieves all entities of this type from the database.
     *
     * @return A list of all entities.
     * @throws SQLException If a database access error occurs.
     */
    List<T> getAll() throws SQLException;
}
