import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        ApartatA a = new ApartatA();
        ApartatB b = new ApartatB();
        b.main();
        System.out.println("Que vol executar l'apartat A o B: ");
        Scanner reader = new Scanner(System.in);
        String s = reader.next();
        reader.close();
        if (s.toLowerCase().equals("a"))
            a.main();
        else
            b.main();
    }


}