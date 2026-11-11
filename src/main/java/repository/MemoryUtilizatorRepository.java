package repository;

import domain.Utilizator;
import validate.Validator;

/**
 * in-memory repository utilizatori
 */
public class MemoryUtilizatorRepository extends InMemoryRepository<Long, Utilizator> {
    public MemoryUtilizatorRepository() { }

    public MemoryUtilizatorRepository(Validator<Utilizator> validator) { }
}
