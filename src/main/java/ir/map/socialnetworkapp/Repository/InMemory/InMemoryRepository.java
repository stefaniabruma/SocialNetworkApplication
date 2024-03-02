package ir.map.socialnetworkapp.Repository.InMemory;


import ir.map.socialnetworkapp.Domain.Entity;
import ir.map.socialnetworkapp.Repository.RepositoryInterfaces.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    Map<ID, E> entities;

    public InMemoryRepository() {
        entities = new HashMap<ID, E>();
    }

    @Override
    public Optional<E> findOne(ID id) {
        if(id == null)
            throw new IllegalArgumentException("Null ID!\n");

        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findALL() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("Null Entity!\n");

        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity));
    }

    @Override
    public Optional<E> delete(ID id) {
        if(id == null)
            throw new IllegalArgumentException("Null ID!\n");

        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("Null Entity!\n");

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(), entity);
            return Optional.empty();
        }

        return Optional.of(entity);

    }
}
