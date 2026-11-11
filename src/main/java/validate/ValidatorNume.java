package validate;
import domain.Utilizator;
import exceptions.ValidationException;

/**
 * valideaza numele unui utilizator
 */
public class ValidatorNume implements Validator<Utilizator> {

    /**
     * creeaza validatorul pentru numele utilizatorului
     */
    public ValidatorNume() {
    }

    /**
     * valideaza numele unui utilizator
     *
     * @param utilizator utilizatorul pentru care se verifica numele
     * @throws ValidationException daca numele e considerat invalid
     */
    @Override
    public void validate(Utilizator utilizator) throws ValidationException {
        if (utilizator.getNume() == null || utilizator.getNume().trim().isEmpty()) {
            throw new ValidationException("Numele nu poate fi gol.");
        }
    }

}