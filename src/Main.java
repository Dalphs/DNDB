import java.net.MalformedURLException;

public class Main {


    public static void main(String[] args) {
        DBConnect connect = new DBConnect();
        for (int i = 0; i < 20; i++) {
            connect.createTransfers();
        }

    }
}
