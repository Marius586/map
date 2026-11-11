package exceptions;

/**
 * exceptie pentru eroare de tip validare
 */
public class ValidationException extends RuntimeException {
    /**
     * creeaza o exceptie cu un mesaj
     *
     * @param mesaj descrierea mesajului de eroare
     */
    public ValidationException(String mesaj) { super(mesaj); }
}