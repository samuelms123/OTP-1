package dao;

import java.util.List;

/**
 * Full IDao Interface for model.
 * @param <T> Generics T for reusability, instead of being tied to specific entity.
 */
public interface IDao<T> {

    public void persist(T entity);
    public T find(int id);
    public List<T> findAll();
    public void deleteAll();
}
