package repository;

import domain.Entity;
import exceptions.RepositoryException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * repository pe fisier configurat cu functii de conversie a entitatilor.
 *
 * @param <ID> tip identificator
 * @param <E> tip entitate
 */
public class FileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private final Path filePath;
    private final Function<String, E> fromLine;
    private final Function<E, String> toLine;

    /**
     * creeaza un repository si incarca entitatile din fisier.
     *
     * @param fileName calea fisierului
     * @param fromLine functia care transforma o linie intr-o entitate
     * @param toLine functia care transforma o entitate intr-o linie
     */
    public FileRepository(String fileName, Function<String, E> fromLine,
                          Function<E, String> toLine) {
        this.filePath = Paths.get(fileName);
        this.fromLine = fromLine;
        this.toLine = toLine;
        loadFromFile();
    }

    @Override
    public void save(E entity) {
        super.save(entity);
        persist();
    }

    @Override
    public void delete(ID id) {
        super.delete(id);
        persist();
    }

    @Override
    public void update(E entity) {
        super.update(entity);
        persist();
    }

    private void loadFromFile() {
        if (!Files.exists(filePath)) {
            return;
        }
        try (Stream<String> lines = Files.lines(filePath)) {
            List<String> allLines = lines.collect(Collectors.toList());
            java.util.stream.IntStream.range(0, allLines.size()).forEach(index -> {
                String line = allLines.get(index);
                if (!line.trim().isEmpty()) {
                    try {
                        E entity = fromLine.apply(line);
                        if (entity == null) {
                            throw new IllegalArgumentException("entitate null");
                        }
                        super.save(entity);
                    } catch (RuntimeException exception) {
                        System.err.println("linie invalida ignorata " + (index + 1) + " in "
                                + filePath + ": " + exception.getMessage());
                    }
                }
            });
        } catch (IOException exception) {
            throw new RepositoryException("nu se poate citi " + filePath, exception);
        }
    }

    private void persist() {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, entities.stream()
                    .map(toLine)
                    .collect(Collectors.toList()));
        } catch (IOException exception) {
            throw new RepositoryException("nu se poate scrie " + filePath, exception);
        }
    }
}
