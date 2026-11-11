package exceptions;

/**
 * exceptie pentru eroare de tip utilizator inexistent
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * creeaza o exceptie cu un mesaj
     *
     * @param mesaj descrierea mesajului de eroare
     */
    public UserNotFoundException(String mesaj) { 
        super(mesaj); 
    }
}