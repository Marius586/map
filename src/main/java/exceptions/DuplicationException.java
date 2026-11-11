package exceptions;

/**
 * exceptie pentru eroare de tip duplicat 
 */
public class DuplicationException extends RuntimeException {
    /**
     * creeaza o exceptie cu un mesaj
     *
     * @param mesaj descrierea mesajului de eroare 
     */
    public DuplicationException(String mesaj) { super(mesaj); }
}