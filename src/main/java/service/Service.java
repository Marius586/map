package service;

import domain.Prietenie;
import domain.Utilizator;
import exceptions.DuplicationException;
import exceptions.UserNotFoundException;
import repository.Repository;
import validate.ValidatorUtilizator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * serviciul aplicatiei.
 */
public class Service {
    private final Repository<Long, Utilizator> users;
    private final Repository<String, Prietenie> friendships;
    private final ValidatorUtilizator userValidator;

    public Service(Repository<Long, Utilizator> users, Repository<String, Prietenie> friendships,
                   ValidatorUtilizator userValidator) {
        this.users = users;
        this.friendships = friendships;
        this.userValidator = userValidator;
    }

    public Utilizator adaugaUtilizator(long id, String nume, String email) {
        Utilizator user = new Utilizator(id, nume, email);
        userValidator.valideaza(user);
        users.save(user);
        return user;
    }

    public Utilizator adaugaUtilizator(String nume, String email) {
        long nextId = StreamSupport.stream(users.findAll().spliterator(), false)
                .mapToLong(Utilizator::getId).max().orElse(0) + 1;
        return adaugaUtilizator(nextId, nume, email);
    }

    public Utilizator updateUser(Long id, String nume,String email){
        Utilizator user = new Utilizator(id,nume,email);
        userValidator.valideaza(user);
        users.update(user);
        return user;
    }

    public void stergeUtilizator(long id) {
        requireUser(id);
        friendships.findAll(friendship -> friendship.getUser1() == id || friendship.getUser2() == id)
                .stream().map(Prietenie::getId).forEach(friendships::delete);
        if (users.delete(id) == null) {
            throw new UserNotFoundException(id + " nu a fost gasit");
        }
    }

    public void adaugaPrietenie(long id1, long id2) {
        requireUser(id1);
        requireUser(id2);
        if (id1 == id2 || friendships.findOne(new Prietenie(id1, id2).getId()) != null) {
            throw new DuplicationException("prietenia deja exista");
        }
        friendships.save(new Prietenie(id1, id2));
    }

    public void stergePrietenie(long id1, long id2) {
        requireUser(id1);
        requireUser(id2);
        friendships.delete(new Prietenie(id1, id2).getId());
    }

    /**
     * cauta utilizatori folosind un predicat compozabil.
     *
     * @param filter filtrul utilizatorilor
     * @return utilizatorii care respecta filtrul
     */
    public List<Utilizator> cautaUtilizatori(Predicate<Utilizator> filter) {
        return users.findAll(filter);
    }

    /**
     * numara prietenii unui utilizator.
     *
     * @param user utilizatorul pentru care se numara prietenii
     * @return numarul de prietenii
     */
    public int numarPrieteni(Utilizator user) {
        return friendships.findAll(friendship -> friendship.getUser1() == user.getId()
                        || friendship.getUser2() == user.getId())
                .size();
    }

    /**
     * returneaza utilizatorii fara prieteni folosind un criteriu reutilizabil.
     *
     * @return utilizatorii fara prieteni
     */
    public List<Utilizator> getUtilizatoriFaraPrieteni() {
        return cautaUtilizatori(CriteriiUtilizator.faraPrieteni(this::numarPrieteni));
    }

    public Optional<Utilizator> cautaUtilizator(long id) {
        return Optional.ofNullable(users.findOne(id));
    }

    public int getNumarComunitati() {
        return components(usersAsList()).size();
    }

    public int getDiametruComunitateSociabila() {
        List<Utilizator> userList = usersAsList();
        return components(userList).stream()
                .mapToInt(component -> diameter(component, userList))
                .max().orElse(0);
    }

    /**
     * returneaza utilizatorii din comunitatea cu diametrul cel mai mare.
     *
     * @return utilizatorii comunitatii celei mai sociabile sau o lista goala daca nu exista utilizatori
     */
    public List<Utilizator> getComunitateSociabila() {
        List<Utilizator> userList = usersAsList();
        Optional<Set<Long>> community = components(userList).stream()
                .max(Comparator.comparingInt(component -> diameter(component, userList)));
        return community.map(selected -> userList.stream()
                        .filter(user -> selected.contains(user.getId()))
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }

    private void requireUser(long id) {
        cautaUtilizator(id).orElseThrow(() ->
                new UserNotFoundException(id + " nu a fost gasit"));
    }

    private List<Utilizator> usersAsList() {
        return StreamSupport.stream(users.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    private List<Set<Long>> components(List<Utilizator> userList) {
        return userList.stream().map(Utilizator::getId).reduce(
                new ArrayList<>(),
                (result, id) -> {
                    if (result.stream().noneMatch(component -> component.contains(id))) {
                        Set<Long> component = new HashSet<>();
                        collectComponent(id, component, userList);
                        result.add(component);
                    }
                    return result;
                },
                (left, right) -> {
                    left.addAll(right);
                    return left;
                });
    }

    private void collectComponent(long id, Set<Long> component, List<Utilizator> userList) {
        if (!component.add(id)) return;
        neighbors(id, userList).stream()
                .filter(neighbor -> !component.contains(neighbor))
                .forEach(neighbor -> collectComponent(neighbor, component, userList));
    }

    private int diameter(Set<Long> component, List<Utilizator> userList) {
        return component.stream()
                .mapToInt(start -> distances(start, userList).entrySet().stream()
                        .filter(entry -> component.contains(entry.getKey()))
                        .mapToInt(Map.Entry::getValue)
                        .max().orElse(0))
                .max().orElse(0);
    }

    private List<Long> neighbors(long id, List<Utilizator> userList) {
        Set<Long> userIds = userList.stream().map(Utilizator::getId).collect(Collectors.toSet());
        return StreamSupport.stream(friendships.findAll().spliterator(), false)
                .flatMap(friendship -> {
                    if (friendship.getUser1() == id) return java.util.stream.Stream.of(friendship.getUser2());
                    if (friendship.getUser2() == id) return java.util.stream.Stream.of(friendship.getUser1());
                    return java.util.stream.Stream.empty();
                })
                .filter(userIds::contains).distinct().collect(Collectors.toList());
    }

    private Map<Long, Integer> distances(long start, List<Utilizator> userList) {
        Map<Long, Integer> result = new HashMap<>();
        result.put(start, 0);
        return expandDistances(Collections.singleton(start), result, userList);
    }

    private Map<Long, Integer> expandDistances(Set<Long> frontier, Map<Long, Integer> distances,
                                                List<Utilizator> userList) {
        Set<Long> next = frontier.stream().flatMap(id -> neighbors(id, userList).stream())
                .filter(id -> !distances.containsKey(id)).collect(Collectors.toSet());
        next.forEach(id -> distances.put(id, frontier.stream()
                .mapToInt(parent -> distances.get(parent) + 1).min().orElse(0)));
        return next.isEmpty() ? distances : expandDistances(next, distances, userList);
    }

    public void printall() {
        StreamSupport.stream(users.findAll().spliterator(), false).forEach(System.out::println);
        System.out.println("------------------");
        StreamSupport.stream(friendships.findAll().spliterator(), false).forEach(System.out::println);
    }
}
