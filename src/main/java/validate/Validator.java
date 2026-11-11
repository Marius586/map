package validate;
import exceptions.ValidationException;

/**
 * Contract functional pentru validarea unei entitati.
 *
 * @param <E> tipul entitatii
 */
@FunctionalInterface
public interface Validator<E> {
    /**
     * Valideaza o entitate.
     *
     * @param entity entitatea de validat
     * @throws ValidationException daca entitatea este invalida
     */
    void validate(E entity) throws ValidationException;

    /**
     * Compune acest validator cu un alt validator.
     *
     * @param other validatorul executat dupa acest validator
     * @return validatorul compus
     */
    default Validator<E> andThen(Validator<E> other) {
        return entity -> {
            validate(entity);
            other.validate(entity);
        };
    }
}