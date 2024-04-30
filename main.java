import java.util.LinkedList;

public class main {

    public static void main (String [] args) {
        realtorsParser z = new realtorsParser();
        LinkedList<house> Redfinhouses = z.collectAllHouses();
        System.out.println(Redfinhouses.size());
    }
}