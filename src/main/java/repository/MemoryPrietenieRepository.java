package repository;

import domain.Prietenie;
import validate.Validator;

/**
 * in-memory repository prietenii
 */
public class MemoryPrietenieRepository extends InMemoryRepository<String, Prietenie> {
    public MemoryPrietenieRepository() { }

    public MemoryPrietenieRepository(Validator<Prietenie> validator) { }
}
