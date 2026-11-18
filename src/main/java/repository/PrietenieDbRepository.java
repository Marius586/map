package repository;

import domain.Prietenie;
import exceptions.RepositoryException;
import validate.Validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC repository for friendships.
 */
public class PrietenieDbRepository implements Repository<String, Prietenie> {
    private final Validator<Prietenie> validator;
    private final String url;
    private final String user;
    private final String password;

    public PrietenieDbRepository(Validator<Prietenie> validator, String url,
                                 String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public void save(Prietenie entity) {
        requireEntity(entity);
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO friendships(id1, id2, friends_from) VALUES (?, ?, ?)")) {
            statement.setLong(1, entity.getUser1());
            statement.setLong(2, entity.getUser2());
            statement.setObject(3, entity.getData());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw databaseError("salvarea prieteniei", exception);
        }
    }

    @Override
    public Prietenie findOne(String id) {
        long[] ids = parseId(id);
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id1, id2, friends_from FROM friendships WHERE id1 = ? AND id2 = ?")) {
            statement.setLong(1, ids[0]);
            statement.setLong(2, ids[1]);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? read(result) : null;
            }
        } catch (SQLException exception) {
            throw databaseError("citirea prieteniei", exception);
        }
    }

    @Override
    public Iterable<Prietenie> findAll() {
        List<Prietenie> result = new ArrayList<>();
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id1, id2, friends_from FROM friendships ORDER BY id1, id2");
             ResultSet rows = statement.executeQuery()) {
            while (rows.next()) {
                result.add(read(rows));
            }
            return result;
        } catch (SQLException exception) {
            throw databaseError("citirea prieteniilor", exception);
        }
    }

    @Override
    public Prietenie delete(String id) {
        long[] ids = parseId(id);
        Prietenie deleted = findOne(id);
        if (deleted == null) {
            return null;
        }
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM friendships WHERE id1 = ? AND id2 = ?")) {
            statement.setLong(1, ids[0]);
            statement.setLong(2, ids[1]);
            statement.executeUpdate();
            return deleted;
        } catch (SQLException exception) {
            throw databaseError("stergerea prieteniei", exception);
        }
    }

    @Override
    public Prietenie update(Prietenie entity) {
        requireEntity(entity);
        if (findOne(entity.getId()) == null) {
            return null;
        }
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE friendships SET friends_from = ? WHERE id1 = ? AND id2 = ?")) {
            statement.setObject(1, entity.getData());
            statement.setLong(2, entity.getUser1());
            statement.setLong(3, entity.getUser2());
            statement.executeUpdate();
            return entity;
        } catch (SQLException exception) {
            throw databaseError("actualizarea prieteniei", exception);
        }
    }

    private Prietenie read(ResultSet result) throws SQLException {
        return new Prietenie(result.getLong("id1"), result.getLong("id2"),
                result.getTimestamp("friends_from").toLocalDateTime());
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private void requireEntity(Prietenie entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entitate null");
        }
        validator.validate(entity);
    }

    private static long[] parseId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id nu poate fi null");
        }
        String[] parts = id.split("-", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("id prietenie invalid");
        }
        try {
            long first = Long.parseLong(parts[0]);
            long second = Long.parseLong(parts[1]);
            if (first >= second) {
                throw new IllegalArgumentException("id prietenie invalid");
            }
            return new long[]{first, second};
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("id prietenie invalid", exception);
        }
    }

    private static RepositoryException databaseError(String operation, SQLException exception) {
        return new RepositoryException("Eroare la " + operation + ": " + exception.getMessage(), exception);
    }
}
