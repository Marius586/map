package validate;

import domain.Utilizator;
import exceptions.ValidationException;

/**
 * valideaza email-ul unui utilizator
 */
public class ValidatorEmail implements Validator<Utilizator> {
    /**
     * creeaza validatorul pentru adresa de email
     */
    public ValidatorEmail() {
    }

    /**
     * valideaza email-ul unui utilizator
     *
     * @param utilizator utilizatorul pentru care se verica adresa de email
     * @throws ValidationException daca email-ul e considerat invalid
     */
    @Override
    public void validate(Utilizator utilizator) throws ValidationException {
        if (utilizator.getEmail() == null || !utilizator.getEmail().contains("@")) {
            throw new ValidationException("Format de email invalid.");
        }
    }
}