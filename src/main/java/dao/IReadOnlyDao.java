package dao;

import java.util.List;

/**
 * Read only IDao for controller.
 *
 * @param <T> Generics T for reusability, instead of being tied to specific entity.
 */
public interface IReadOnlyDao<T> {
    public T find(int id);

    public List<T> findAll();
}
