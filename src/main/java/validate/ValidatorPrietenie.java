package validate;

import domain.Prietenie;
import domain.Utilizator;
import exceptions.UserNotFoundException;
import repository.Repository;

/**
 * valideaza prietenia
 */
public class ValidatorPrietenie implements Validator<Prietenie> {
    private final Repository<Long, Utilizator> users;

    public ValidatorPrietenie(Repository<Long, Utilizator> users) {
        this.users = users;
    }

    @Override
    public void validate(Prietenie friendship) {
        if (friendship.getUser1() == friendship.getUser2()) {
            throw new IllegalArgumentException("o prietenie contine 2 persoane");
        }
        if (users.findOne(friendship.getUser1()) == null
                || users.findOne(friendship.getUser2()) == null) {
            throw new UserNotFoundException("unul din utilizatori nu exista");
        }
    }
}
