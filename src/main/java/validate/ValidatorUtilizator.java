package validate;
import domain.Utilizator;
import exceptions.ValidationException;

/**
 * valideaza utilizatorul cu toate regulile de validare primite
 */
public class ValidatorUtilizator implements Validator<Utilizator> {
    private final Validator<Utilizator>[] validatori;
    /**
     * creaza lista regulilor de validare
     *
     * @param validator lista regulilor de validare
     */
    public ValidatorUtilizator(Validator<Utilizator>[] validator) {
        this.validatori = validator;
    }
    /**
     * verifica toate regulile de validare
     *
     * @param utilizator utilizator de validat
     * @throws ValidationException daca orice regula e invalida
     */
    public void valideaza(Utilizator utilizator) throws ValidationException {
        for (int i = 0; i < validatori.length; i++) {
            validatori[i].validate(utilizator);
        }
    }

    @Override
    public void validate(Utilizator utilizator) {
        valideaza(utilizator);
    }
}