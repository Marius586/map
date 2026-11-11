package repository;

import domain.Utilizator;
import validate.Validator;

/**
 * schelet baza de date utilizator
 */
public class UtilizatorDbRepository extends InMemoryRepository<Long, Utilizator> {
    public UtilizatorDbRepository(Validator<Utilizator> validator, String url,
                                  String user, String password) { }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("neimplementat");
    }

    @Override public void save(Utilizator entity) { throw unsupported(); }
    @Override public Utilizator findOne(Long id) { throw unsupported(); }
    @Override public Iterable<Utilizator> findAll() { throw unsupported(); }
    @Override public void delete(Long id) { throw unsupported(); }
    @Override public void update(Utilizator entity) { throw unsupported(); }
}
