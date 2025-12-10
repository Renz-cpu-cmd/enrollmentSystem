package dao;

import java.util.List;

/**
 * A generic interface for Data Access Objects (DAOs).
 * It defines the standard CRUD (Create, Read, Update, Delete) operations.
 *
 * @param <T> The type of the model class this DAO works with.
 * @param <K> The type of the primary key of the model class.
 */
public interface DataAccessObject<T, K> {

    /**
     * Retrieves a single object by its primary key.
     *
     * @param id The primary key of the object to retrieve.
     * @return The object if found, otherwise null.
     */
    T getById(K id);

    /**
     * Retrieves all objects of this type from the data source.
     *
     * @return A list of all objects.
     */
    List<T> getAll();

    /**
     * Saves a new object to the data source.
     *
     * @param t The object to save.
     * @return true if the operation was successful, false otherwise.
     */
    boolean add(T t);

    /**
     * Updates an existing object in the data source.
     *
     * @param t The object to update.
     * @return true if the operation was successful, false otherwise.
     */
    boolean update(T t);

    /**
     * Deletes an object from the data source by its primary key.
     *
     * @param id The primary key of the object to delete.
     * @return true if the operation was successful, false otherwise.
     */
    boolean delete(K id);
}
