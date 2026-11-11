package repository;

import domain.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * repository generic in-memory
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    protected final List<E> entities = new ArrayList<E>();

    @Override
    public void save(E entity) {
        if (entity == null) throw new IllegalArgumentException("entitate null");
        if (findOne(entity.getId()) != null) {
            throw new IllegalArgumentException("entitate cu id:" + entity.getId() + " deja exista");
        }
        entities.add(entity);
    }

    @Override
    public E findOne(ID id) {
        if (id == null) throw new IllegalArgumentException("id nu poate fi null");
        return entities.stream().filter(entity -> id.equals(entity.getId()))
                .findFirst().orElse(null);
    }

    @Override
    public Iterable<E> findAll() {
        return new ArrayList<E>(entities);
    }

    @Override
    public void delete(ID id) {
        if (id == null) throw new IllegalArgumentException("id nu poate fi null.");
        E entity = findOne(id);
        if (entity == null) throw new IllegalArgumentException("entitate cu id:" + id + " nu exista");
        entities.remove(entity);
    }

    @Override
    public void update(E entity) {
        if (entity == null) throw new IllegalArgumentException("entitate null");
        Optional<Integer> index = java.util.stream.IntStream.range(0, entities.size())
                .filter(i -> entity.getId().equals(entities.get(i).getId()))
                .boxed().findFirst();
        if (index.isPresent()) {
            entities.set(index.get(), entity);
        } else {
            throw new IllegalArgumentException("entitate cu id:" + entity.getId() + " nu exista");
        }
    }
}
