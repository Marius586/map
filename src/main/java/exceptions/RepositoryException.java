package exceptions;

/**
 * exceptie in repository
 */
public class RepositoryException extends RuntimeException {
    /**
     * @param message mesaj de eroare
     * @param exceptie java exception
     */
    public RepositoryException(String message, Throwable exceptie) {
        super(message, exceptie);
    }
}
