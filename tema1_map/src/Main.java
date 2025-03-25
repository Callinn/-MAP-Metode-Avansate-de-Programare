import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Introduceți numărul de elemente în expresie (inclusiv operatorii):");
        int n = scanner.nextInt();

        String[] input = new String[n];
        scanner.nextLine();

        for (int i = 0; i < n; i++) {
            System.out.println("Introduceți elementul " + (i + 1) + " (număr complex sau operator):");
            input[i] = scanner.nextLine();
        }

        // Evaluăm expresia
        try {
            ComplexNumber result = ComplexExpression.evaluate(input);
            System.out.println("Rezultatul: " + result);
        } catch (Exception e) {
            System.out.println("Eroare la evaluarea expresiei: " + e.getMessage());
        }

        scanner.close();  // Închidem scannerul
    }
}
// a(+-)bi
//a si b nr reale