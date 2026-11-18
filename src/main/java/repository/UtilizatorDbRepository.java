package repository;

import domain.Utilizator;
import exceptions.RepositoryException;
import validate.Validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC repository for users.
 */
public class UtilizatorDbRepository implements Repository<Long, Utilizator> {
    private final Validator<Utilizator> validator;
    private final String url;
    private final String user;
    private final String password;

    public UtilizatorDbRepository(Validator<Utilizator> validator, String url,
                                  String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public void save(Utilizator entity) {
        requireEntity(entity);
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO users(first_name, last_name) VALUES (?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getNume());
            statement.setString(2, entity.getEmail());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new RepositoryException("baza de date nu a generat id", null);
                }
                entity.setId(keys.getLong(1));
            }
        } catch (SQLException exception) {
            throw databaseError("salvarea utilizatorului", exception);
        }
    }

    @Override
    public Utilizator findOne(Long id) {
        requireId(id);
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, first_name, last_name FROM users WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? read(result) : null;
            }
        } catch (SQLException exception) {
            throw databaseError("citirea utilizatorului", exception);
        }
    }

    @Override
    public Iterable<Utilizator> findAll() {
        List<Utilizator> result = new ArrayList<>();
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, first_name, last_name FROM users ORDER BY id");
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                result.add(read(rows));
            }
            return result;
        } catch (SQLException exception) {
            throw databaseError("citirea utilizatorilor", exception);
        }
    }

    @Override
    public Utilizator delete(Long id) {
        requireId(id);
        Utilizator deleted = findOne(id);
        if (deleted == null) {
            return null;
        }
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM users WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
            return deleted;
        } catch (SQLException exception) {
            throw databaseError("stergerea utilizatorului", exception);
        }
    }

    @Override
    public Utilizator update(Utilizator entity) {
        requireEntity(entity);
        if (findOne(entity.getId()) == null) {
            return null;
        }
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?")) {
            statement.setString(1, entity.getNume());
            statement.setString(2, entity.getEmail());
            statement.setLong(3, entity.getId());
            statement.executeUpdate();
            return entity;
        } catch (SQLException exception) {
            throw databaseError("actualizarea utilizatorului", exception);
        }
    }

    private Utilizator read(ResultSet result) throws SQLException {
        return new Utilizator(result.getLong("id"), result.getString("first_name"),
                result.getString("last_name"));
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private void requireEntity(Utilizator entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entitate null");
        }
        validator.validate(entity);
    }

    private static void requireId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id nu poate fi null");
        }
    }

    private static RepositoryException databaseError(String operation, SQLException exception) {
        return new RepositoryException("Eroare la " + operation + ": " + exception.getMessage(), exception);
    }
}
