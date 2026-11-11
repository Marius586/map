Laboratorul 4 - refactorizare Java 8

Alegerea repository-ului:
- Repository.findOne pastreaza contractul din laboratorul 3 si poate intoarce null.
- Service.cautaUtilizator expune Optional pentru operatiile care pot sa nu gaseasca rezultat.
- Am folosit compozitie: FileRepository<ID,E> primeste Function<String,E> pentru conversia
  linie-entitate si Function<E,String> pentru conversia entitate-linie. Astfel persistenta
  este implementata o singura data, iar formatul specific ramane configurat in factory.

Inainte / dupa:
1. InMemoryRepository.findOne: for-each -> filter, findFirst, Optional.orElse.
2. InMemoryRepository.update: for index -> IntStream.range, filter, findFirst.
3. FileRepository.loadFromFile: while -> Files.lines, collect, IntStream.range.
4. FileRepository.persist: for-each -> map, collect.
5. Service.stergeUtilizator: for-each -> filter, map, collect, forEach.
6. Service.getNumarComunitati: parcurgere indexata -> stream, reduce si DFS recursiv.
7. Service.getDiametruComunitateSociabila: for/while -> mapToInt, max si BFS recursiv.

Interfete functionale:
- Validator este FunctionalInterface si are andThen.
- Repository.findAll primeste Predicate.
- RepositoryFactory foloseste Map<RepositoryType, Supplier<Repository>>.
- CriteriiUtilizator ofera Predicate, iar cautaUtilizatori accepta predicate compozabile.
- Se folosesc referinte la metode precum Prietenie::getId, Utilizator::getId,
  System.out::println si constructori in factory.

Buclele clasice ramase:
- while-ul meniului din Main este controlul interactiv al aplicatiei, nu parcurgerea unei
  colectii. Switch-ul meniului reprezinta dispatch-ul comenzilor si nu este o transformare
  de colectie.
