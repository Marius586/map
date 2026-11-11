package repository;

import domain.Prietenie;
import domain.Utilizator;
import exceptions.RepositoryException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.BiFunction;
import java.time.LocalDateTime;
import validate.Validator;

/**
 * factory repository
 */
public final class RepositoryFactory {
    private final RepositoryType type;
    private final String usersPath;
    private final String friendshipsPath;
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    private RepositoryFactory(Properties properties) {
        String typeValue = required(properties, "repository.type");
        try {
            type = RepositoryType.valueOf(typeValue.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new RepositoryException("repository.type: " + typeValue, exception);
        }
        usersPath = required(properties, "file.users");
        friendshipsPath = required(properties, "file.friendships");
        dbUrl = required(properties, "db.url");
        dbUser = properties.getProperty("db.user", "");
        dbPassword = properties.getProperty("db.password", "");
    }

    /**
     * incarca config file
     *
     * @return factory cu configuratie
     */
    public static RepositoryFactory fromConfiguration() {
        Properties properties = new Properties();
        try (InputStream input = RepositoryFactory.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) throw new RepositoryException("config.properties lipsa", null);
            properties.load(input);
            return new RepositoryFactory(properties);
        } catch (IOException exception) {
            throw new RepositoryException("config.properties", exception);
        }
    }

    public Repository<Long, Utilizator> createUtilizatorRepository(Validator<Utilizator> validator) {
        BiFunction<String, Validator<Utilizator>, Utilizator> userParser =
                RepositoryFactory::parseUser;
        Map<RepositoryType, Supplier<Repository<Long, Utilizator>>> repositories =
                new EnumMap<>(RepositoryType.class);
        repositories.put(RepositoryType.MEMORY, MemoryUtilizatorRepository::new);
        repositories.put(RepositoryType.FILE, () -> new FileRepository<>(
                usersPath,
                line -> userParser.apply(line, validator),
                Utilizator::toString));
        repositories.put(RepositoryType.DATABASE,
                () -> new UtilizatorDbRepository(validator, dbUrl, dbUser, dbPassword));
        return repositoryFor(repositories);
    }

    public Repository<String, Prietenie> createPrietenieRepository(
            Validator<Prietenie> validator, Repository<Long, Utilizator> users) {
        BiFunction<String, Validator<Prietenie>, Prietenie> friendshipParser =
                RepositoryFactory::parseFriendship;
        Map<RepositoryType, Supplier<Repository<String, Prietenie>>> repositories =
                new EnumMap<>(RepositoryType.class);
        repositories.put(RepositoryType.MEMORY, MemoryPrietenieRepository::new);
        repositories.put(RepositoryType.FILE, () -> new FileRepository<>(
                friendshipsPath,
                line -> friendshipParser.apply(line, validator),
                Prietenie::toString));
        repositories.put(RepositoryType.DATABASE,
                () -> new PrietenieDbRepository(validator, dbUrl, dbUser, dbPassword));
        return repositoryFor(repositories);
    }

    private <ID, E extends domain.Entity<ID>> Repository<ID, E> repositoryFor(
            Map<RepositoryType, Supplier<Repository<ID, E>>> repositories) {
        Supplier<Repository<ID, E>> supplier = repositories.get(type);
        if (supplier == null) throw new RepositoryException("tip repository necunoscut: " + type, null);
        return supplier.get();
    }

    private static Utilizator parseUser(String line, Validator<Utilizator> validator) {
        String[] fields = line.split(";", -1);
        if (fields.length != 3) throw new IllegalArgumentException("format id;nume;email");
        Utilizator user = new Utilizator(Long.parseLong(fields[0].trim()),
                fields[1].trim(), fields[2].trim());
        validator.validate(user);
        return user;
    }

    private static Prietenie parseFriendship(String line, Validator<Prietenie> validator) {
        String[] fields = line.split(";", -1);
        if (fields.length != 3) throw new IllegalArgumentException("expected id1;id2;date");
        Prietenie friendship = new Prietenie(Long.parseLong(fields[0].trim()),
                Long.parseLong(fields[1].trim()), LocalDateTime.parse(fields[2].trim()));
        validator.validate(friendship);
        return friendship;
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RepositoryException("configuratie lipsa: " + key, null);
        }
        return value;
    }
}
