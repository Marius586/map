package domain;

/**
 * contract de baza pentru entitatile domeniului stocate in repository-uri
 *
 * @param <ID> tipul identificatorului entitatii
 */
public interface Entity<ID> {
    /**
     * returneaza identificator entitate
     *
     * @return identificator entitate
     */
    ID getId();
}
