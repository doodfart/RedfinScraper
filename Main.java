import java.util.LinkedList;

public class Main {

    public static void main (String [] args) {
        RealtorsParser z = new RealtorsParser();
        LinkedList<House> Redfinhouses = z.collectAllHouses();
        System.out.println(Redfinhouses.size());
    }
}