package service;

import domain.Utilizator;

import java.util.function.Predicate;

/**
 * Criterii reutilizabile pentru cautarea utilizatorilor.
 */
public final class CriteriiUtilizator {
    private CriteriiUtilizator() {
    }

    /**
     * creeaza un predicat pentru utilizatorii al caror nume contine textul primit.
     *
     * @param text textul cautat
     * @return predicatul de selectie
     */
    public static Predicate<Utilizator> numeContine(String text) {
        return user -> user.getNume().toLowerCase().contains(text.toLowerCase());
    }

    /**
     * creeaza un predicat pentru utilizatorii fara prieteni.
     *
     * @param friendCount functia care furnizeaza numarul de prieteni
     * @return predicatul de selectie
     */
    public static Predicate<Utilizator> faraPrieteni(
            java.util.function.ToIntFunction<Utilizator> friendCount) {
        return user -> friendCount.applyAsInt(user) == 0;
    }
}
