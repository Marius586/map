import exceptions.DuplicationException;
import exceptions.RepositoryException;
import exceptions.UserNotFoundException;
import exceptions.ValidationException;
import domain.Prietenie;
import domain.Utilizator;
import repository.Repository;
import repository.RepositoryFactory;
import service.CriteriiUtilizator;
import service.Service;
import validate.Validator;
import validate.ValidatorEmail;
import validate.ValidatorNume;
import validate.ValidatorPrietenie;
import validate.ValidatorUtilizator;
import java.util.Scanner;

/**
 * start aplicatie
 */
public class Main {
    public static void main(String[] args) {
        Validator<Utilizator>[] validators = (Validator<Utilizator>[]) new Validator[]{
                new ValidatorNume().andThen(user -> {
                    if (user.getId() <= 0) {
                        throw new ValidationException("Id-ul trebuie sa fie pozitiv.");
                    }
                }), new ValidatorEmail()
        };
        ValidatorUtilizator userValidator = new ValidatorUtilizator(validators);
        try {
            RepositoryFactory factory = RepositoryFactory.fromConfiguration();
            Repository<Long, Utilizator> users = factory.createUtilizatorRepository(userValidator);
            Repository<String, Prietenie> friendships = factory.createPrietenieRepository(
                    new ValidatorPrietenie(users), users);
            Service service = new Service(users, friendships, userValidator);
            meniu(service);
        } catch (RepositoryException exception) {
            System.out.println("repository error: " + exception.getMessage());
        } catch (UnsupportedOperationException exception) {
            System.out.println("neimplementat: " + exception.getMessage());
        }
    }

    private static void meniu(Service service) {
        Scanner scanner = new Scanner(System.in);
        boolean run = true;
        while (run) {
            System.out.println();
            System.out.println("1 adauga utilizator");
            System.out.println("2 sterge utilizator");
            System.out.println("3 adauga prietenie");
            System.out.println("4 sterge prietenie");
            System.out.println("5 numar comunitati");
            System.out.println("6 cea mai sociabila comunitate (diametru)");
            System.out.println("7 cauta utilizatori dupa nume");
            System.out.println("8 utilizatori fara prieteni");
            System.out.println("0 exit");
            String option = scanner.nextLine();
            try {
                switch (option) {
                    case "1":
                        System.out.print("id: ");
                        long id = Long.parseLong(scanner.nextLine());
                        System.out.print("nume: ");
                        String name = scanner.nextLine();
                        System.out.print("email: ");
                        service.adaugaUtilizator(id, name, scanner.nextLine());
                        System.out.println("utilizator adaugat");
                        break;
                    case "2":
                        System.out.print("id: ");
                        service.stergeUtilizator(Long.parseLong(scanner.nextLine()));
                        System.out.println("utilizator sters");
                        break;
                    case "3":
                        System.out.print("id 1: ");
                        long first = Long.parseLong(scanner.nextLine());
                        System.out.print("id 2: ");
                        service.adaugaPrietenie(first, Long.parseLong(scanner.nextLine()));
                        System.out.println("prietenie adaugata");
                        break;
                    case "4":
                        System.out.print("id 1: ");
                        long left = Long.parseLong(scanner.nextLine());
                        System.out.print("id 2: ");
                        service.stergePrietenie(left, Long.parseLong(scanner.nextLine()));
                        System.out.println("prietenie stearsa");
                        break;
                    case "5":
                        System.out.println("comunitati: " + service.getNumarComunitati());
                        break;
                    case "6":
                        // System.out.println("diametru: " + service.getDiametruComunitateSociabila());
                        service.getComunitateSociabila().forEach(System.out::println);
                        break;
                    case "7":
                        System.out.print("text nume: ");
                        service.cautaUtilizatori(
                                        CriteriiUtilizator.numeContine(scanner.nextLine()))
                                .forEach(System.out::println);
                        break;
                    case "8":
                        service.getUtilizatoriFaraPrieteni().forEach(System.out::println);
                        break;
                    case "99":
                        service.printall();
                        break;
                    case "0":
                        run = false;
                        break;
                    default:
                        System.out.println("optiune invalida");
                }
            } catch (NumberFormatException exception) {
                System.out.println("id-uri trebuie sa fie numere");
            } catch (ValidationException | UserNotFoundException | DuplicationException
                     | IllegalArgumentException exception) {
                System.out.println("error: " + exception.getMessage());
            }
        }
        scanner.close();
    }
}
