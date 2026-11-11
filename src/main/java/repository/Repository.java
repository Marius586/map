package repository;

import domain.Entity;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * generic contract repository.
 *
 * @param <ID> tip identificator
 * @param <E> tip entitate
 */
public interface Repository<ID, E extends Entity<ID>> {
    /**
     * salveaza o entitate
     *
     * @param entity entitatea de salvat
     * @throws IllegalArgumentException daca entitatea este null
     */
    void save(E entity);

    /**
     * cauta o entitate dupa id
     *
     * @param id id-ul cautat
     * @return entitatea gasita sau null
     * @throws IllegalArgumentException daca id-ul este null
     */
    E findOne(ID id);

    /**
     * returneaza toate entitatile
     *
     * @return entitati
     */
    Iterable<E> findAll();

    /**
     * returneaza toate entitatile acceptate de predicatul primit.
     *
     * @param filter predicatul de selectie
     * @return entitatile care respecta predicatul
     */
    default List<E> findAll(Predicate<E> filter) {
        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * sterge o entitate
     *
     * @param id id-ul de sters
     * @throws IllegalArgumentException daca id-ul este null
     */
    void delete(ID id);

    /**
     * actualizeaza o entitate
     *
     * @param entity entitate noua
     * @throws IllegalArgumentException daca entitatea este null sau inexistenta
     */
    void update(E entity);
}
