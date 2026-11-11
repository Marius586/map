package domain;

import java.util.Objects;

/**
 * entitate de tip utilizator
 */
public class Utilizator implements Entity<Long> {
    private final long id;
    private final String nume;
    private final String email;

    /**
     * creeaza un utilizator.
     *
     * @param id id utilizator
     * @param nume numele utilizator
     * @param email email utilizator
     */
    public Utilizator(long id, String nume, String email) {
        this.id = id;
        this.nume = nume;
        this.email = email;
    }

    @Override
    public Long getId() { 
        return id; 
    }
    
    public String getNume() { 
        return nume; 
    }
    
    public String getEmail() { 
        return email; 
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Utilizator other1)) return false;
        return id == other1.id;
    }

    @Override
    public int hashCode() { 
        return Objects.hash(this.toString()); 
    }

    @Override
    public String toString() { 
        return id + ";" + nume + ";" + email; 
    }
}
