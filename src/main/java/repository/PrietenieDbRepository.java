package repository;

import domain.Prietenie;
import validate.Validator;

/**
 * schelet baza de date prietenii
 */
public class PrietenieDbRepository extends InMemoryRepository<String, Prietenie> {
    public PrietenieDbRepository(Validator<Prietenie> validator, String url,
                                 String user, String password) { }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("baza de date neimplementata");
    }

    @Override public void save(Prietenie entity) { throw unsupported(); }
    @Override public Prietenie findOne(String id) { throw unsupported(); }
    @Override public Iterable<Prietenie> findAll() { throw unsupported(); }
    @Override public void delete(String id) { throw unsupported(); }
    @Override public void update(Prietenie entity) { throw unsupported(); }
}
