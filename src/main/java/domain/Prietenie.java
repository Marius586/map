package domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * relatie de prietenie
 */
public class Prietenie implements Entity<String> {
    private final long user1;
    private final long user2;
    private final LocalDateTime data;

    /**
     * creeaza o prietenie la data curenta
     *
     * @param user1 id utilizator 1
     * @param user2 id utilizator 2
     */
    public Prietenie(long user1, long user2) {
        this(user1, user2, LocalDateTime.now());
    }

    /**
     * creeaza o prietenie
     *
     * @param user1 id utilizator 1
     * @param user2 id utilizator 2
     * @param dataPrieteniei data crearii prieteniei
     */
    public Prietenie(long user1, long user2, LocalDateTime dataPrieteniei) {
        if (user1 <= user2) {
            this.user1 = user1;
            this.user2 = user2;
        } else {
            this.user1 = user2;
            this.user2 = user1;
        }
        this.data = Objects.requireNonNull(dataPrieteniei);
    }

    public long getUser1() { return user1; }
    public long getUser2() { return user2; }
    public LocalDateTime getData() { return data; }

    @Override
    public String getId() {
        return user1 + "-" + user2;
    }

    @Override
    public String toString() {
        return user1 + ";" + user2 + ";" + data;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Prietenie other1)) return false;
        return user1 == other1.user1 && user2 == other1.user2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2);
    }
}
